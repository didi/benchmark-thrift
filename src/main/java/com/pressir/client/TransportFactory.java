package com.pressir.client;


import com.google.common.net.HostAndPort;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

/**
 * @ClassName TransportFactory
 * @Description TODO
 * @Author didi
 * @Date 2019-09-03 10:07
 */
public abstract class TransportFactory {

    /**
     * Get Transport
     *
     * @param hostAndPort
     * @return
     */
    public abstract TTransport getTransport(HostAndPort hostAndPort);

    /**
     * 获取TTransport
     *
     * @return
     */

    public static class SocketFactory extends TransportFactory {
        int socketTimeout;
        int connectTimeout;

        public SocketFactory() {
            this(0, 0);
        }

        public SocketFactory(int socketTimeout, int connectTimeout) {
            this.socketTimeout = socketTimeout;
            this.connectTimeout = connectTimeout;
        }

        @Override
        public TTransport getTransport(HostAndPort hostAndPort) {
            return new TSocket(hostAndPort.getHost(), hostAndPort.getPort(), this.socketTimeout, this.connectTimeout);
        }


    }

    public static class FramedTransportFactory extends TransportFactory {

        TransportFactory base;

        int maxLength;

        public FramedTransportFactory() {
            this(new SocketFactory(), 16384000);
        }

        public FramedTransportFactory(TransportFactory base, int maxLength) {
            this.base = base;
            this.maxLength = maxLength;
        }

        @Override
        public TTransport getTransport(HostAndPort hostAndPort) {
            return new TFramedTransport(base.getTransport(hostAndPort), maxLength);
        }
    }
}
