package com.pressir.utils;


import com.pressir.idl.Soda;
import com.pressir.idl.service.Service;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class ClientUtilsTest {

    @Before
    public void before() {
        new Thread(() -> {
            try {
                Service.run();
            } catch (TTransportException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Test
    public void test_open_close_client() throws InterruptedException, TTransportException {
        TimeUnit.SECONDS.sleep(1);
        TTransport tTransport = new TSocket("127.0.0.1", 8090);
        TFramedTransport tFramedTransport = new TFramedTransport(tTransport);
        TCompactProtocol iprot = new TCompactProtocol(tTransport);
        tTransport.open();
        TCompactProtocol oprot = new TCompactProtocol(tFramedTransport);
        Soda.Client client = new Soda.Client(iprot, oprot);
        ClientUtils.open(client);
        Assert.assertTrue(ClientUtils.isOpen(client));
        ClientUtils.close(client);

        TCompactProtocol protocol = new TCompactProtocol(tFramedTransport);
        Soda.Client client1 = new Soda.Client(protocol);
        ClientUtils.open(client1);
        Assert.assertTrue(ClientUtils.isOpen(client1));
        ClientUtils.close(client1);
    }

}
