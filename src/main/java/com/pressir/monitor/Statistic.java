package com.pressir.monitor;


import com.pressir.constant.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.pressir.constant.Constants.MAX_TIME_SPENT;

/**
 * @ClassName Statistic
 * @Description 统计结果
 * @Author pressir
 * @Date 2019-08-28 10:43
 */
class Statistic {
    private static final Logger LOGGER = LoggerFactory.getLogger(Statistic.class);
    private final int interval;
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
    private List<Integer> timeSpent = new ArrayList<>();

    Statistic(int interval) {
        this.interval = interval;
    }

    private void calculatePercentage() {
        if (responses == 0) {
            return;
        }
        if (timeSpent.size() == 0) {
            return;
        }
        if (timeSpent.size() == 1) {
            p50 = p75 = p90 = p95 = p99 = timeSpent.get(0);
        }
        if (max - min <= MAX_TIME_SPENT) {
            //桶排序
            int[] bucketNums = bucketSort();
            int nums = 0;
            for (int i = 0; i < bucketNums.length; i++) {
                nums = nums + bucketNums[i];
                if (nums >= requests.get() * 0.50 && p50 == 0) {
                    p50 = min + i;
                }
                if (nums >= requests.get() * 0.75 && p75 == 0) {
                    p75 = min + i;
                }
                if (nums >= requests.get() * 0.90 && p90 == 0) {
                    p90 = min + i;
                }
                if (nums >= requests.get() * 0.95 && p95 == 0) {
                    p95 = min + i;
                }
                if (nums >= requests.get() * 0.99 && p99 == 0) {
                    p99 = min + i;
                    break;
                }
            }
        } else {
            //直接快排
            Collections.sort(timeSpent);
            p50 = timeSpent.get((int) (responses * 0.50));
            p75 = timeSpent.get((int) (responses * 0.75));
            p90 = timeSpent.get((int) (responses * 0.90));
            p95 = timeSpent.get((int) (responses * 0.95));
            p99 = timeSpent.get((int) (responses * 0.99));
        }
    }

    private int[] bucketSort() {
        int[] bucket = new int[max - min + 1];
        for (int time : timeSpent) {
            int index = time - min;
            bucket[index]++;
        }
        return bucket;
    }

    void onSend() {
        requests.getAndIncrement();
        connects.getAndDecrement();
    }

    void onReceived(int time) {
        if (max < time) {
            max = time;
        }
        if (min > time) {
            min = time;
        }
        synchronized (this) {
            timeTaken += time;
            timeSpent.add(time);
            responses += 1;
        }
        if (responses % interval == 0) {
            LOGGER.info("\tCompleted {} requests", responses);
        }
    }

    void onError(Exception e, String msg) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.error("ErrMsg: {} , Maybe caused by {}", e.getMessage() == null ? e.getStackTrace()[0].toString() : e.getMessage(), msg);
        }
    }

    void onStop() {
        // 计算分位耗时
        calculatePercentage();
        // 计算成功率
        if (requests.get() != 0) {
            sucRateWCS = (double) responses / requests.get() * 100;
            sucRateWCS = (double) ((int) (sucRateWCS * 100)) / 100;
        }

        sucRateCCS = (double) responses / (requests.get() + connects.get()) * 100;
        sucRateCCS = (double) ((int) (sucRateCCS * 100)) / 100;

        // 计算耗时
        if (responses != 0) {
            latency = (double) timeTaken / responses;
            latency = (double) ((int) (latency * 100)) / 100;
        }
        // 输出报告
        LOGGER.info(this.toString());
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (responses % interval != 0) {
            stringBuilder.append("\tCompleted ").append(responses).append(" requests\n");
        }
        stringBuilder.append("\tFinished\n")
                .append("Time taken for successful requests: ").append((double) timeTaken / Constants.TIME_CONVERT_BASE).append(" seconds\n")
                .append("On connection stat: ").append(connects.get()).append("\n")
                .append("Send requests: ").append(requests.get()).append("\n")
                .append("Complete requests: ").append(responses).append("\n")
                .append("Failed requests: ").append(requests.get() - responses).append("\n")
                .append("Success rate without connection stat: ").append(sucRateWCS).append("%\n")
                .append("Success rate contains connection stat: ").append(sucRateCCS).append("%\n")
                .append("Time per request: ").append(latency == 0 ? "--" : latency).append(" [ms] \n")
                .append("Minimum time taken ").append(min == Integer.MAX_VALUE ? "--" : min).append(" [ms]\n")
                .append("Maximum time taken ").append(max == Integer.MIN_VALUE ? "--" : max).append(" [ms]\n")
                .append("Percentage of the requests served within a certain time (ms)\n ")
                .append("\t50% ").append(p50 == Integer.MAX_VALUE ? "--" : p50).append("\n")
                .append("\t75% ").append(p75 == Integer.MAX_VALUE ? "--" : p75).append("\n")
                .append("\t90% ").append(p90 == Integer.MAX_VALUE ? "--" : p90).append("\n")
                .append("\t95% ").append(p95 == Integer.MAX_VALUE ? "--" : p95).append("\n")
                .append("\t99% ").append(p99 == Integer.MAX_VALUE ? "--" : p99);
        return stringBuilder.toString();
    }

    void onConnect() {
        connects.getAndIncrement();
    }
}
