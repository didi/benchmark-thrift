package com.didiglobal.pressir.thrift.base.transport;

import com.didiglobal.pressir.thrift.context.ContextParser;
import org.junit.Assert;
import org.junit.Test;


public class TSocketFactoryTest {

    @Test
    public void shouldReturnTSocketFactoryDefault() {
        TTransportFactory factory = ContextParser.parseTransportFactory("TSocket");
        Assert.assertTrue(factory instanceof TSocketFactory);
    }

    @Test
    public void shouldReturnTSocketFactoryWithTimeoutSet() {
        TTransportFactory factory = ContextParser.parseTransportFactory("TSocket(timeout=1)");
        Assert.assertTrue(factory instanceof TSocketFactory);
    }

    @Test
    public void shouldReturnTSocketFactoryWithAllTimeoutSet() {
        TTransportFactory factory = ContextParser.parseTransportFactory("TSocket(socketTimeout=1, connectTimeout=1)");
        Assert.assertTrue(factory instanceof TSocketFactory);
    }

}
