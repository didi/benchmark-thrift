package com.pressir.base.transport;

import com.pressir.context.ContextParser;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

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
