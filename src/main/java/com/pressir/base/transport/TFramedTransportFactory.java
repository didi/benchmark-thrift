package com.pressir.base.transport;

import com.google.common.net.HostAndPort;
import com.pressir.context.ContextParser;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TTransport;

import java.util.Collections;
import java.util.Map;

/**
 * @ClassName TFramedTransportFactory
 * @Description TODO
 * @Author didi
 * @Date 2019-09-19 21:15
 */
public class TFramedTransportFactory implements TTransportFactory<TFramedTransport> {
    private final TTransportFactory transportFactory;

    private final TFramedTransport.Factory framedTransportFactory;

    public TFramedTransportFactory(Map<String, String> attrs) {
        String transportAttr = attrs.get("transport");
        //TSocketFactory for default
        if (transportAttr == null) {
            this.transportFactory = new TSocketFactory(Collections.emptyMap());
        } else {
            this.transportFactory = ContextParser.parseTransportFactory(transportAttr);
        }
        this.framedTransportFactory = new TFramedTransport.Factory();
        ContextParser.setField(this.framedTransportFactory, attrs);
    }

    @Override
    public TFramedTransport getTransport(HostAndPort endpoint) {
        TTransport transport = this.transportFactory.getTransport(endpoint);
        return (TFramedTransport) this.framedTransportFactory.getTransport(transport);
    }

}
