package com.pressir.client;

import com.alibaba.fastjson.util.IOUtils;
import com.google.common.io.Closeables;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TTransport;


/**
 * @ClassName ClientFactory
 * @Description The Manager of Client
 * @Author pressir
 * @Date 2019-09-03 10:34
 */
public class ClientFactory<T extends TServiceClient> {

    private final TServiceClientFactory<T> serviceClientFactory;

    private final TProtocolFactory protocolFactory;

    private final TransportFactory transportFactory;

    public ClientFactory(TServiceClientFactory<T> serviceClientFactory, TProtocolFactory protocolFactory, TransportFactory transportFactory) {
        this.serviceClientFactory = serviceClientFactory;
        this.protocolFactory = protocolFactory;
        this.transportFactory = transportFactory;
    }

    /**
     * get A client
     *
     * @return
     */
    public T getClient() {
        TTransport transport = this.transportFactory.getTransport(OneHostAndPort.getOne());
        TProtocol protocol = this.protocolFactory.getProtocol(transport);
        return this.serviceClientFactory.getClient(protocol);
    }

    /**
     * close client
     *
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
