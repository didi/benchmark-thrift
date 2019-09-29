package com.didiglobal.pressir.thrift.monitor;


import com.didiglobal.pressir.thrift.console.ConsolePrinter;
import com.didiglobal.pressir.thrift.constant.Constants;
import org.apache.logging.log4j.util.Strings;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName Statistic
 * @Description 统计结果
 * @Author pressir
 * @Date 2019-08-28 10:43
 */
class Statistic {
    private static final Logger LOGGER = LoggerFactory.getLogger(Statistic.class);

    private static final Map<Integer, String> TRANSPORT_EXCEPTION_TYPE_MAP = new HashMap<>();

    private static final Map<Integer, String> PROTOCOL_EXCEPTION_TYPE_MAP = new HashMap<>();

    private static final Map<Integer, String> APPLICATION_EXCEPTION_TYPE_MAP = new HashMap<>();

    private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();

    private static final Map<String, Integer> EXCEPTION_COUNT_MAP = new HashMap<>();

    private static final Map<String, Integer> EXCEPTION_MAP = new HashMap<>();

    static {
        TRANSPORT_EXCEPTION_TYPE_MAP.put(0, "Unknown");
        TRANSPORT_EXCEPTION_TYPE_MAP.put(1, "Socket not open");
        TRANSPORT_EXCEPTION_TYPE_MAP.put(2, "Socket already open(connected)");
        TRANSPORT_EXCEPTION_TYPE_MAP.put(3, "Time out");
        TRANSPORT_EXCEPTION_TYPE_MAP.put(4, "End of file! Check connection or protocol type");
        TRANSPORT_EXCEPTION_TYPE_MAP.put(5, "Corrupted data! Maybe read a negative frame size");

        PROTOCOL_EXCEPTION_TYPE_MAP.put(0, "Unknown");
        PROTOCOL_EXCEPTION_TYPE_MAP.put(1, "Required field types must be consistent");
        PROTOCOL_EXCEPTION_TYPE_MAP.put(2, "Negative size");
        PROTOCOL_EXCEPTION_TYPE_MAP.put(3, "Size limit");
        PROTOCOL_EXCEPTION_TYPE_MAP.put(4, "Bad version! Maybe caused by protocol type or read strict");
        PROTOCOL_EXCEPTION_TYPE_MAP.put(5, "This protocol does not support (yet)");
        PROTOCOL_EXCEPTION_TYPE_MAP.put(6, "Depth limit");

        APPLICATION_EXCEPTION_TYPE_MAP.put(0, "Unknown");
        APPLICATION_EXCEPTION_TYPE_MAP.put(1, "Unknown method");
        APPLICATION_EXCEPTION_TYPE_MAP.put(2, "Invalid message type");
        APPLICATION_EXCEPTION_TYPE_MAP.put(3, "Wrong method name");
        APPLICATION_EXCEPTION_TYPE_MAP.put(4, "Bad sequence id");
        APPLICATION_EXCEPTION_TYPE_MAP.put(5, "Missing result");
        APPLICATION_EXCEPTION_TYPE_MAP.put(6, "Internal error");
        APPLICATION_EXCEPTION_TYPE_MAP.put(7, "Protocol error");
        APPLICATION_EXCEPTION_TYPE_MAP.put(8, "Invalid transform");
        APPLICATION_EXCEPTION_TYPE_MAP.put(9, "Invalid protocol");
        APPLICATION_EXCEPTION_TYPE_MAP.put(10, "Unsupported client type");

        EXCEPTION_COUNT_MAP.put("APPLICATION_EXCEPTION", 0);
        EXCEPTION_COUNT_MAP.put("PROTOCOL_EXCEPTION", 0);
        EXCEPTION_COUNT_MAP.put("TRANSPORT_EXCEPTION", 0);
        EXCEPTION_COUNT_MAP.put("OTHERS", 0);
    }

    private int min = Integer.MAX_VALUE;
    private int max = Integer.MIN_VALUE;
    private int p99 = Integer.MAX_VALUE;
    private int p95 = Integer.MAX_VALUE;
    private int p90 = Integer.MAX_VALUE;
    private int p75 = Integer.MAX_VALUE;
    private int p50 = Integer.MAX_VALUE;
    private double sucRateWCS = 0;
    private double sucRateCCS = 0;
    private double latency = 0;
    private AtomicInteger connects = new AtomicInteger(0);
    private AtomicInteger requests = new AtomicInteger(0);
    private int responses = 0;
    private int timeTaken = 0;
    private Map<Integer, Integer> timeAndCounts = new ConcurrentHashMap<>();

    Statistic(int interval) {
        if (interval <= 0) {
            interval = 1;
        }
        EXECUTOR.scheduleAtFixedRate(() ->
                        ConsolePrinter.say("\t{}\t{}\t{}\t{}\t{}\t{}",
                                this.requests.get(),
                                this.responses,
                                EXCEPTION_COUNT_MAP.get("TRANSPORT_EXCEPTION"),
                                EXCEPTION_COUNT_MAP.get("PROTOCOL_EXCEPTION"),
                                EXCEPTION_COUNT_MAP.get("APPLICATION_EXCEPTION"),
                                EXCEPTION_COUNT_MAP.get("OTHERS")),
                interval + 1,
                interval,
                TimeUnit.SECONDS);
    }

    private int[] bucketSort(int max, int min, int interval, Map<Integer, Integer> timeAndCounts) {
        int[] bucket;
        if (interval > 1) {
            bucket = new int[10000];
            for (Map.Entry<Integer, Integer> entry : timeAndCounts.entrySet()) {
                bucket[(entry.getKey() - min) / interval] += entry.getValue();
            }
        } else {
            bucket = new int[max - min + 1];
            for (Map.Entry<Integer, Integer> entry : timeAndCounts.entrySet()) {
                bucket[entry.getKey() - min] += entry.getValue();
            }
        }
        return bucket;
    }

    private void calculatePercentage() {
        if (this.responses == 0) {
            return;
        }
        if (this.timeAndCounts.size() == 1) {
            for (Map.Entry<Integer, Integer> entry : timeAndCounts.entrySet()) {
                this.p50 = this.p75 = this.p90 = this.p95 = this.p99 = entry.getValue();
            }
            return;
        }

        int sum = 0;
        int interval = (this.max - this.min) / 10000 + 1;
        int[] bucket = bucketSort(this.max, this.min, interval, this.timeAndCounts);
        for (int i = 0; i < bucket.length; i++) {
            sum += bucket[i];
            if (this.p50 == Integer.MAX_VALUE && sum >= this.responses * 0.50) {
                this.p50 = this.min + i * interval;
            }
            if (this.p75 == Integer.MAX_VALUE && sum >= this.responses * 0.75) {
                this.p75 = this.min + i * interval;
            }
            if (this.p90 == Integer.MAX_VALUE && sum >= this.responses * 0.90) {
                this.p90 = this.min + i * interval;
            }
            if (this.p95 == Integer.MAX_VALUE && sum >= this.responses * 0.95) {
                this.p95 = this.min + i * interval;
            }
            if (this.p99 == Integer.MAX_VALUE && sum >= this.responses * 0.99) {
                this.p99 = this.min + i * interval;
                break;
            }
        }
    }

    void onSend() {
        this.requests.getAndIncrement();
        this.connects.getAndDecrement();
    }

    synchronized void onReceived(int time) {
        if (this.max < time) {
            this.max = time;
        }
        if (this.min > time) {
            this.min = time;
        }
        this.timeTaken += time;
        this.responses += 1;
        Integer count = this.timeAndCounts.get(time);
        if (count == null) {
            count = 0;
        }
        this.timeAndCounts.put(time, ++count);
    }

    synchronized void onError(Exception e) {
        if (e instanceof InvocationTargetException) {
            Throwable throwable = ((InvocationTargetException) e).getTargetException();
            if (throwable instanceof TTransportException) {
                int count = EXCEPTION_COUNT_MAP.get("TRANSPORT_EXCEPTION");
                EXCEPTION_COUNT_MAP.put("TRANSPORT_EXCEPTION", ++count);
                int type = ((TTransportException) throwable).getType();
                String msg = TRANSPORT_EXCEPTION_TYPE_MAP.get(type);
                statisticalErrCount("TTransportException[" + type + ", Msg: " + msg + "]");
                onError(throwable, msg);
            }
            if (throwable instanceof TProtocolException) {
                int count = EXCEPTION_COUNT_MAP.get("PROTOCOL_EXCEPTION");
                EXCEPTION_COUNT_MAP.put("PROTOCOL_EXCEPTION", ++count);
                int type = ((TProtocolException) throwable).getType();
                String msg = PROTOCOL_EXCEPTION_TYPE_MAP.get(type);
                statisticalErrCount("TProtocolException[" + type + ", Msg: " + msg + "]");
                onError(throwable, msg);
            }
            if (throwable instanceof TApplicationException) {
                int count = EXCEPTION_COUNT_MAP.get("APPLICATION_EXCEPTION");
                EXCEPTION_COUNT_MAP.put("APPLICATION_EXCEPTION", ++count);
                int type = ((TApplicationException) throwable).getType();
                String msg = APPLICATION_EXCEPTION_TYPE_MAP.get(type);
                statisticalErrCount("TApplicationException[" + type + ", Msg: " + msg + "]");
                onError(throwable, msg);
            }
        } else {
            int count = EXCEPTION_COUNT_MAP.get("OTHERS");
            count++;
            EXCEPTION_COUNT_MAP.put("OTHERS", count);
            statisticalErrCount(e.getMessage());
            onError(e, null);
        }
    }

    private synchronized void onError(Throwable e, String msg) {
        if (Strings.isBlank(msg)) {
            LOGGER.error("ErrMsg: {}", e.getMessage() == null ? e.getStackTrace()[0].toString() : e.getMessage());
        } else {
            LOGGER.error("ErrMsg: {} , Maybe caused by {}",
                    e.getMessage() == null ?
                            e.getStackTrace()[0].toString() :
                            e.getMessage(),
                    msg);
        }
    }

    private void statisticalErrCount(String errorMsg) {
        Integer msgCount = EXCEPTION_MAP.get(errorMsg);
        if (msgCount == null) {
            msgCount = 0;
        }
//        if (msgCount > 100) {
//            LOGGER.info("There are too many mistakes({}). Please check them first!", errorMsg);
//            System.exit(1);
//        }
        EXCEPTION_MAP.put(errorMsg, ++msgCount);
    }


    void onStop() {
        EXECUTOR.shutdown();
        // 计算分位耗时
        this.calculatePercentage();
        // 计算成功率
        if (this.requests.get() != 0) {
            this.sucRateWCS = (double) this.responses / this.requests.get() * 100;
            this.sucRateWCS = (double) ((int) (this.sucRateWCS * 100)) / 100;
        }

        this.sucRateCCS = (double) this.responses / (this.requests.get() + this.connects.get()) * 100;
        this.sucRateCCS = (double) ((int) (this.sucRateCCS * 100)) / 100;

        // 计算耗时
        if (this.responses != 0) {
            this.latency = (double) this.timeTaken / this.responses;
            this.latency = (double) ((int) (this.latency * 100)) / 100;
        }
        // 输出报告
        ConsolePrinter.say(this.toString());
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\tFinished\n")
                .append("Time taken for successful requests: ")
                .append((double) this.timeTaken / Constants.TIME_CONVERT_BASE)
                .append(" seconds\n")
                .append("On connection stat: ")
                .append(this.connects.get())
                .append("\n")
                .append("Send requests: ")
                .append(this.requests.get())
                .append("\n")
                .append("Complete requests: ")
                .append(this.responses)
                .append("\n")
                .append("Failed requests: ")
                .append(this.requests.get() - this.responses)
                .append("\n");
        if (EXCEPTION_MAP.size() > 0) {
            stringBuilder.append("\t");
            for (Map.Entry<String, Integer> entry : EXCEPTION_MAP.entrySet()) {
                stringBuilder.append(entry.getKey()).append(": ")
                        .append(entry.getValue())
                        .append("\n");
            }
        }
        stringBuilder.append("Success rate without connection stat: ")
                .append(this.sucRateWCS)
                .append("%\n")
                .append("Success rate contains connection stat: ")
                .append(this.sucRateCCS)
                .append("%\n")
                .append("Time per request: ")
                .append(this.latency == 0 ? "--" : this.latency)
                .append(" [ms] \n")
                .append("Minimum time taken ")
                .append(this.min == Integer.MAX_VALUE ? "--" : this.min)
                .append(" [ms]\n")
                .append("Maximum time taken ")
                .append(this.max == Integer.MIN_VALUE ? "--" : this.max)
                .append(" [ms]\n")
                .append("Percentage of the requests served within a certain time (ms)\n ")
                .append("\t50% ")
                .append(this.p50 == Integer.MAX_VALUE ? "--" : this.p50)
                .append("\n")
                .append("\t75% ")
                .append(this.p75 == Integer.MAX_VALUE ? "--" : this.p75)
                .append("\n")
                .append("\t90% ")
                .append(this.p90 == Integer.MAX_VALUE ? "--" : this.p90)
                .append("\n")
                .append("\t95% ")
                .append(this.p95 == Integer.MAX_VALUE ? "--" : this.p95)
                .append("\n")
                .append("\t99% ")
                .append(this.p99 == Integer.MAX_VALUE ? "--" : this.p99);
        return stringBuilder.toString();
    }

    void onConnect() {
        this.connects.getAndIncrement();
    }
}
