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

    /**
     * 获取TTransport
     *
     * @return
     */
    public TTransport getTransport() {
        return null;
    }

    public static class TSocketFactory extends TTransportFactory {

        public TSocketFactory(HostAndPort hostAndPort) {
            super(hostAndPort);
        }

        @Override
        public TTransport getTransport() {
            return new TSocket(this.hostAndPort.getHost(), this.hostAndPort.getPort());
        }
    }

    public static class TFramedTransportFactory extends TTransportFactory {

        public TFramedTransportFactory(HostAndPort hostAndPort) {
            super(hostAndPort);
        }

        @Override
        public TTransport getTransport() {
            return new TFramedTransport(new TSocket(this.hostAndPort.getHost(), this.hostAndPort.getPort()));
        }
    }
}
