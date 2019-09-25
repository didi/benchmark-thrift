package com.didiglobal.pressir.thrift.base.transport;
import com.google.common.net.HostAndPort;
import org.apache.thrift.transport.TSocket;

import java.util.Map;
/**
 * @ClassName TSocketFactory
 * @Description TODO
 * @Author pressir
 * @Date 2019-09-19 21:13
 */
public class TSocketFactory implements TTransportFactory<TSocket> {

    private final int socketTimeout;

    private final int connectTimeout;

    public TSocketFactory(Map<String, String> attrs) {
        int socketTimeout = 0;
        int connectTimeout = 0;
        String timeoutAttr = attrs.get("timeout");
        String socketTimeoutAttr = attrs.get("socketTimeout");
        String connectTimeoutAttr = attrs.get("connectTimeout");
        if (timeoutAttr != null) {
            int timeout = Integer.parseInt(timeoutAttr);
            socketTimeout = timeout;
            connectTimeout = timeout;
        }
        if (socketTimeoutAttr != null) {
            socketTimeout = Integer.parseInt(socketTimeoutAttr);
        }
        if (connectTimeoutAttr != null) {
            connectTimeout = Integer.parseInt(connectTimeoutAttr);
        }
        this.socketTimeout = socketTimeout;
        this.connectTimeout = connectTimeout;
    }

    @Override
    public TSocket getTransport(HostAndPort endpoint) {
        return new TSocket(endpoint.getHost(), endpoint.getPort(), this.socketTimeout, this.connectTimeout);
    }
}

