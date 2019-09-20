package com.pressir.base.transport;

import com.google.common.net.HostAndPort;
import org.apache.thrift.transport.TTransport;

/**
 * @InterfaceName TTransportFactory
 * @Description TODO
 * @Author didi
 * @Date 2019-09-19 21:14
 */
public interface TTransportFactory<T extends TTransport> {
    T getTransport(HostAndPort endpoint);
}
