package com.pressir.generator;

import com.pressir.client.BaseClientFactory;
import com.pressir.client.Request;
import com.pressir.constant.Constants;
import com.pressir.monitor.Monitor;
import com.pressir.utils.ClientUtils;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName InvariantTaskGenerator
 * @Description TODO
 * @Author pressir
 * @Date 2019-09-03 09:49
 */
public class InvariantTaskGenerator<T extends TServiceClient> implements Generator {

    private static final Logger LOGGER = LoggerFactory.getLogger(InvariantTaskGenerator.class);
    private static final Map<Integer, String> TRANSPORT_EXCEPTION_TYPE_MAP = new HashMap<>();
    private static final Map<Integer, String> PROTOCOL_EXCEPTION_TYPE_MAP = new HashMap<>();
    private static final Map<Integer, String> APPLICATION_EXCEPTION_TYPE_MAP = new HashMap<>();
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
    }
    private final BaseClientFactory<T> clientFactory;
    private final Method method;
    private final Object[] args;
    private final Class<?> result;

    private InvariantTaskGenerator(BaseClientFactory<T> clientFactory, Method method, Object[] args, Class<?> result) {
        this.clientFactory = clientFactory;
        this.method = method;
        this.args = args;
        this.result = result;
    }

    public static <T extends TServiceClient> Generator newInstance(BaseClientFactory<T> clientFactory, Request request) {
        return new InvariantTaskGenerator<>(clientFactory, request.getMethod(), request.parseArguments(), request.getResult());
    }

    private static <T extends TServiceClient> String getKeyword(Class<T> clientClass, Method method) {
        return clientClass.getDeclaringClass().getSimpleName() + "." + method.getName();
    }

    @Override
    public List<Runnable> generate(int num) {
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            tasks.add(this::execute);
        }
        return tasks;
    }

    private void execute() {
        T client = this.clientFactory.getClient();
        String keyword = getKeyword(client.getClass(), this.method);
        try {
            long connectStartAt = System.currentTimeMillis();
            if (!ClientUtils.isOpen(client)) {
                Monitor.onConnect(keyword);
                //socket connection
                this.clientFactory.open(client);
                long connectStopAt = System.currentTimeMillis();
                if (connectStopAt - connectStartAt > Constants.CONNECT_THRESHOLD) {
                    LOGGER.error("Connected cost time {}", connectStopAt - connectStartAt);
                }
            }
            Monitor.onSend(keyword);
            Object obj = this.method.invoke(client, this.args);
            Monitor.onReceived(keyword, (int) (System.currentTimeMillis() - connectStartAt));
//            System.out.println(result.cast(obj).toString());
        } catch (Exception e) {
            onException(keyword, e);
        } finally {
            this.clientFactory.close(client);
        }
    }


    private void onException(String keyword, Exception t) {
        if (t instanceof TException) {
            if (t instanceof TTransportException) {
                Monitor.onError(keyword, t, TRANSPORT_EXCEPTION_TYPE_MAP.get(((TTransportException) t).getType()));
            } else if (t instanceof TProtocolException) {
                Monitor.onError(keyword, t, PROTOCOL_EXCEPTION_TYPE_MAP.get(((TProtocolException) t).getType()));
            } else if (t instanceof TApplicationException) {
                Monitor.onError(keyword, t, APPLICATION_EXCEPTION_TYPE_MAP.get(((TApplicationException) t).getType()));
            }
        }
    }
}
