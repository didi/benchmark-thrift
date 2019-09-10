package com.pressir.client;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import com.pressir.utils.ClientUtils;
import org.apache.thrift.transport.TTransportException;

/**
 * @ClassName BaseClientFactory
 * @Description The Manager of Client
 * @Author pressir
 * @Date 2019-09-03 10:34
 */
public abstract class BaseClientFactory<T extends TServiceClient> {

    private final TServiceClientFactory<T> serviceClientFactory;

    private final TProtocolFactory tProtocolFactory;

    private final TTransportFactory tTransportFactory;

    BaseClientFactory(TServiceClientFactory<T> serviceClientFactory, TProtocolFactory protocolFactory, TTransportFactory transportFactory) {
        this.serviceClientFactory = serviceClientFactory;
        this.tProtocolFactory = protocolFactory;
        this.tTransportFactory = transportFactory;
    }

    /**
     * get A client
     * @return
     */
    public abstract T getClient();

    T getClient0() {
        return serviceClientFactory.getClient(tProtocolFactory.getProtocol(tTransportFactory.getTransport()));
    }

    public abstract void open(T client) throws TTransportException;

    void open0(T client) throws TTransportException {
        ClientUtils.open(client);
    }

    /**
     * close client
     * @param client
     */
    public abstract void close(T client);

    void close0(T client){
        ClientUtils.close(client);
    }
}
