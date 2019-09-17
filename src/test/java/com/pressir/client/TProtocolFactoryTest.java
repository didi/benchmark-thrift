package com.pressir.client;


import com.google.common.net.HostAndPort;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.junit.Before;
import org.junit.Test;


public class TProtocolFactoryTest {
    private TTransport tTransport;


    @Before
    public void before() {
        HostAndPort hostAndPort = HostAndPort.fromString("127.0.0.1:8090");
        tTransport = new TSocket(hostAndPort.getHost(),hostAndPort.getPort());
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_given_invalid_protocol() {
        String protocol = "InvalidProtocol";
        TProtocolFactory.valueOf(protocol).getProtocol(tTransport);
    }
}
