package com.pressir.generator;

import com.pressir.client.ClientFactory;
import com.pressir.client.Request;
import com.pressir.constant.Constants;
import com.pressir.monitor.Monitor;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
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

    private final ClientFactory<T> clientFactory;
    private final Method method;
    private final Object[] args;
    private final Class<?> result;

    private InvariantTaskGenerator(ClientFactory<T> clientFactory, Method method, Object[] args, Class<?> result) {
        this.clientFactory = clientFactory;
        this.method = method;
        this.args = args;
        this.result = result;
    }

    public static <T extends TServiceClient> Generator newInstance(ClientFactory<T> clientFactory, Request request) {
        return new InvariantTaskGenerator<>(clientFactory, request.getMethod(), request.parseArguments(), request.getResult());
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
        String keyword = this.method.getName();
        T client = this.clientFactory.getClient();
        try {
            long startAt = System.currentTimeMillis();
            if (!client.getInputProtocol().getTransport().isOpen()) {
                client.getInputProtocol().getTransport().open();
                Monitor.onConnect(keyword);
                long connectCost = System.currentTimeMillis() - startAt;
                if (connectCost > Constants.CONNECT_THRESHOLD) {
                    LOGGER.debug("Connected cost time {}", connectCost);
                }
            }
            Monitor.onSend(keyword);
            LOGGER.debug("Request() prepare to send!", this.args);
            Object obj = this.method.invoke(client, this.args);
            Monitor.onReceived(keyword, (int) (System.currentTimeMillis() - startAt));
            LOGGER.debug("Response() has received!", this.result.cast(obj));
        } catch (Exception e) {
            if (e instanceof InvocationTargetException) {
                Throwable throwable = ((InvocationTargetException) e).getTargetException();
                if (throwable instanceof TTransportException) {
                    Monitor.onError(keyword, e, TRANSPORT_EXCEPTION_TYPE_MAP.get(((TTransportException) throwable).getType()));
                }
                if (throwable instanceof TProtocolException) {
                    Monitor.onError(keyword, e, PROTOCOL_EXCEPTION_TYPE_MAP.get(((TProtocolException) throwable).getType()));
                }
                if (throwable instanceof TApplicationException) {
                    Monitor.onError(keyword, e, APPLICATION_EXCEPTION_TYPE_MAP.get(((TApplicationException) throwable).getType()));
                }
            } else {
                Monitor.onError(keyword, e, null);
            }
        } finally {
            this.clientFactory.close(client);
        }
    }
}
