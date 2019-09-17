package com.pressir.context;


import com.alibaba.fastjson.JSON;
import com.pressir.client.ClientFactory;
import com.pressir.client.TransportFactory;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.protocol.TProtocolFactory;

import java.util.Map;


/**
 * @ClassName ThriftContext
 * @Description 配置文件内容
 * @Author pressir
 * @Date 2019-08-30 16:31
 */

public class ThriftContext {

    private TransportContext transportContext;

    private ProtocolContext protocolContext;

    private String jar;

    private ThriftContext(String jar, ProtocolContext protocolContext, TransportContext transportContext) {
        this.jar = jar;
        this.transportContext = transportContext;
        this.protocolContext = protocolContext;
    }

    public static ThriftContext parse(Map<String, Object> map) {
        Object jarObj = map.get("jar");
        Object protocolObj = map.get("protocol");
        if (protocolObj == null) {
            throw new IllegalArgumentException("Must pointed the protocol");
        }

        Object transportObj = map.get("transport");
        if (transportObj == null) {
            throw new IllegalArgumentException("Must pointed the transport");
        }
        return new ThriftContext((String) jarObj,
                JSON.parseObject(JSON.toJSONString(protocolObj), ProtocolContext.class),
                JSON.parseObject(JSON.toJSONString(transportObj), TransportContext.class));
    }

    public <T extends TServiceClient> ClientFactory getClientFactory(TServiceClientFactory<T> serviceClientFactory) {
        TProtocolFactory protocolFactory = this.protocolContext.getFactory();
        TransportFactory transportFactory = this.transportContext.getFactory();
        return new ClientFactory<>(serviceClientFactory, protocolFactory, transportFactory);
    }

    public String getJar() {
        return jar;
    }
}
