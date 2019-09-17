package com.pressir.client;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;

/**
 * @ClassName BaseClientFactory
 * @Description The Manager of Client
 * @Author pressir
 * @Date 2019-09-03 10:34
 */
public abstract class BaseClientFactory<T extends TServiceClient> {

    private final TServiceClientFactory<T> serviceClientFactory;

    private final TProtocolFactory protocolFactory;

    private final TTransportFactory transportFactory;

    BaseClientFactory(TServiceClientFactory<T> serviceClientFactory, TProtocolFactory protocolFactory, TTransportFactory transportFactory) {
        this.serviceClientFactory = serviceClientFactory;
        this.protocolFactory = protocolFactory;
        this.transportFactory = transportFactory;
    }

    /**
     * get A client
     *
     * @return
     */
    public abstract T getClient();

    T getClient0() {
        TTransport transport = this.transportFactory.getTransport();
        TProtocol protocol = this.protocolFactory.getProtocol(transport);
        return this.serviceClientFactory.getClient(protocol);
    }

    /**
     * close client
     * @param client
     */
    public void close(T client) {
        TProtocol iprot = client.getInputProtocol();
        TProtocol oprot = client.getOutputProtocol();
        if (oprot == iprot) {
            iprot.getTransport().close();
            return;
        }
        TTransport itrans = iprot.getTransport();
        TTransport otrans = oprot.getTransport();
        if (otrans == itrans) {
            itrans.close();
            return;
        }
        try {
            itrans.close();
            otrans.close();
        } finally {
            otrans.close();
        }
    }
}
