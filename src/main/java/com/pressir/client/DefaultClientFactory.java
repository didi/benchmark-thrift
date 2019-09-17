package com.pressir.client;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.transport.TTransportException;


/**
 * @ClassName DefaultClientFactory
 * @Description TODO
 * @Author didi
 * @Date 2019-09-03 10:40
 */
public class DefaultClientFactory<T extends TServiceClient> extends BaseClientFactory<T> {


    public DefaultClientFactory(TServiceClientFactory<T> serviceClientFactory, TProtocolFactory protocolFactory, TTransportFactory transportFactory) {
        super(serviceClientFactory, protocolFactory, transportFactory);
    }

    @Override
    public T getClient() {
        return getClient0();
    }
}
