package com.pressir.context;

import org.apache.thrift.protocol.TSimpleJSONProtocol;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProtocolContextTest {

    @Test
    public void should_return_TSimpleProtocolFactory() {
        ProtocolContext protocolContext = new ProtocolContext();
        protocolContext.setType("TSimpleJSONProtocol");
        Assert.assertEquals(TSimpleJSONProtocol.Factory.class, protocolContext.getFactory().getClass());
    }


    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_when_given_wrong_type() {
        ProtocolContext protocolContext = new ProtocolContext();
        protocolContext.setType("InvalidProtocol");
        protocolContext.getFactory();
    }
}
