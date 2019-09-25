package com.didiglobal.pressir.thrift.base.transport;

import com.didiglobal.pressir.thrift.context.ContextParser;
import org.junit.Assert;
import org.junit.Test;

public class TFramedTransportFactoryTest {

    @Test
    public void should_return_TFramedTransportFactory_default() {
        TTransportFactory factory = ContextParser.parseTransportFactory("TFramedTransport");
        Assert.assertTrue(factory instanceof TFramedTransportFactory);
    }

    @Test
    public void should_return_TSocketFactory_with_transport_set() {
        TTransportFactory factory = ContextParser.parseTransportFactory("TFramedTransport(transport=TSocket)");
        Assert.assertTrue(factory instanceof TFramedTransportFactory);
    }

    @Test
    public void should_return_TSocketFactory_with_allfileds_set() {
        TTransportFactory factory = ContextParser.parseTransportFactory("TFramedTransport(transport=TSocket, maxLength=100)");
        Assert.assertTrue(factory instanceof TFramedTransportFactory);
    }

}
