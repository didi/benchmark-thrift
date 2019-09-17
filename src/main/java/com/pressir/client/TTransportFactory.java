package com.pressir.client;


import com.google.common.net.HostAndPort;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;


/**
 * @ClassName TTransportFactory
 * @Description TODO
 * @Author didi
 * @Date 2019-09-03 10:07
 */
public class TTransportFactory {

    HostAndPort hostAndPort;

    TTransportFactory(HostAndPort hostAndPort) {
        this.hostAndPort = hostAndPort;
    }

    TTransportFactory() {
    }


    /**
     * 获取TTransport
     *
     * @return
     */
    public TTransport getTransport() {
        return null;
    }

    public static class TSocketFactory extends TTransportFactory {
        int socketTimeout;
        int connectTimeout;

        public TSocketFactory(HostAndPort hostAndPort, int socketTimeout, int connectTimeout) {
            super(hostAndPort);
            this.socketTimeout = socketTimeout;
            this.connectTimeout = connectTimeout;
        }

        @Override
        public TTransport getTransport() {
            return new TSocket(this.hostAndPort.getHost(), this.hostAndPort.getPort(), this.socketTimeout, this.connectTimeout);
        }
    }

    public static class TFramedTransportFactory extends TTransportFactory {

        TTransportFactory factory;
        int maxLength;

        public TFramedTransportFactory(TTransportFactory factory, int maxLength) {
            this.factory = factory;
            this.maxLength = maxLength;
        }

        @Override
        public TTransport getTransport() {
            return new TFramedTransport(this.factory.getTransport(), this.maxLength);
        }
    }
}
