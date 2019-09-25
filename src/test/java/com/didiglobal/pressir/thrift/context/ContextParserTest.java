package com.didiglobal.pressir.thrift.context;


import com.didiglobal.pressir.thrift.base.transport.TFramedTransportFactory;
import com.didiglobal.pressir.thrift.base.transport.TSocketFactory;
import com.didiglobal.pressir.thrift.base.transport.TTransportFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.protocol.TSimpleJSONProtocol;
import org.apache.thrift.protocol.TTupleProtocol;
import org.junit.Assert;
import org.junit.Test;

public class ContextParserTest {
    @Test
    public void should_return_TSocketFactory() {
        TTransportFactory factory = ContextParser.parseTransportFactory("TSocket(socketTimeout=1, connectTimeout=1)");
        Assert.assertTrue(factory instanceof TSocketFactory);
    }

    @Test
    public void should_return_TFramedTransportFactory() {
        TTransportFactory factory = ContextParser.parseTransportFactory("TFramedTransport(transport=TSocket(timeout=1), maxLength=100)");
        Assert.assertTrue(factory instanceof TFramedTransportFactory);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_when_set_error_transport() {
        ContextParser.parseTransportFactory("TxxxTransport");
    }

    @Test
    public void should_return_TBinaryProtocolFactory() {
        TProtocolFactory factory = ContextParser.parseProtocolFactory("TBinaryProtocol");
        Assert.assertTrue(factory instanceof TBinaryProtocol.Factory);
    }

    @Test
    public void should_return_TCompactProtocolFactory() {
        TProtocolFactory factory = ContextParser.parseProtocolFactory("TCompactProtocol");
        Assert.assertTrue(factory instanceof TCompactProtocol.Factory);
    }

    @Test
    public void should_return_TTupleProtocolFactory() {
        TProtocolFactory factory = ContextParser.parseProtocolFactory("TTupleProtocol");
        Assert.assertTrue(factory instanceof TTupleProtocol.Factory);
    }

    @Test
    public void should_return_TJSONProtocolFactory() {
        TProtocolFactory factory = ContextParser.parseProtocolFactory("TJSONProtocol");
        Assert.assertTrue(factory instanceof TJSONProtocol.Factory);
    }

    @Test
    public void should_return_TSimpleJSONProtocolFactory() {
        TProtocolFactory factory = ContextParser.parseProtocolFactory("TSimpleJSONProtocol");
        Assert.assertTrue(factory instanceof TSimpleJSONProtocol.Factory);
    }



    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_when_set_error_protocol() {
        ContextParser.parseProtocolFactory("TxxxProtocol");
    }

}
