package com.didiglobal.pressir.thrift.base.transport;

import com.google.common.net.HostAndPort;
import org.apache.thrift.transport.TSocket;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

/**
 * @ClassName TSocketFactory
 * @Description TODO
 * @Author pressir
 * @Date 2019-09-19 21:13
 */
public class TSocketFactory implements TTransportFactory<TSocket> {

    private final int[] timeouts;

    private final Constructor<TSocket> constructor;

    public TSocketFactory(Map<String, String> attrs) {
        this.timeouts = parseTimeout(attrs);
        Class<?>[] parameterTypes = new Class<?>[this.timeouts.length + 2];
        parameterTypes[0] = String.class;
        parameterTypes[1] = int.class;
        for (int i = 0; i < this.timeouts.length; i++) {
            parameterTypes[2 + i] = int.class;
        }
        try {
            this.constructor = TSocket.class.getConstructor(String.class, int.class, int.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Invalid construct method TSocket(" + Arrays.toString(parameterTypes) + ")");
        }
    }

    @Override
    public TSocket getTransport(HostAndPort endpoint) {
        Object[] initargs = new Object[this.timeouts.length + 2];
        initargs[0] = endpoint.getHost();
        initargs[1] = endpoint.getPort();
        for (int i = 0; i < this.timeouts.length; i++) {
            initargs[2 + i] = this.timeouts[i];
        }
        try {
            return this.constructor.newInstance(initargs);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    private static int[] parseTimeout(Map<String, String> attrs) {
        String socketTimeoutAttr = attrs.get("socketTimeout");
        if (socketTimeoutAttr == null) {
            String connectTimeoutAttr = attrs.get("connectTimeout");
            if (connectTimeoutAttr != null) {
                int socketTimeout = toInt(attrs.get("timeout"), 0);
                int connectTimeout = Integer.parseInt(connectTimeoutAttr);
                return new int[]{socketTimeout, connectTimeout};
            }
            String timeoutAttr = attrs.get("timeout");
            if (timeoutAttr == null) {
                return new int[0];
            }
            int timeout = Integer.parseInt(timeoutAttr);
            return new int[]{timeout};
        } else {
            int socketTimeout = Integer.parseInt(socketTimeoutAttr);
            int connectTimeout;
            String connectTimeoutAttr = attrs.get("connectTimeout");
            if (connectTimeoutAttr != null) {
                connectTimeout = Integer.parseInt(connectTimeoutAttr);
            } else {
                connectTimeout = toInt(attrs.get("timeout"), 0);
            }
            return new int[]{socketTimeout, connectTimeout};
        }
    }

    private static int toInt(String s, int defaultValue) {
        if (s == null) {
            return defaultValue;
        } else {
            return Integer.parseInt(s);
        }
    }
}

