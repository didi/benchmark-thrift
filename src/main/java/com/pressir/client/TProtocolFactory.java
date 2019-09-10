package com.pressir.client;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;

/**
 * @EnumName TProtocolFactory
 * @Description TODO
 * @Author didi
 * @Date 2019-09-02 23:01
 */
public enum TProtocolFactory {
    TBinaryProtocol(){
        @Override
        public TProtocol getProtocol(TTransport tTransport) {
            return new org.apache.thrift.protocol.TBinaryProtocol(tTransport);
        }
    },
    TCompactProtocol(){
        @Override
        public TProtocol getProtocol(TTransport tTransport) {
            return new org.apache.thrift.protocol.TCompactProtocol(tTransport);
        }
    },
    TJSONProtocol(){
        @Override
        public TProtocol getProtocol(TTransport tTransport) {
            return new org.apache.thrift.protocol.TJSONProtocol(tTransport);
        }
    },
    TSimpleJSONProtocol(){
        @Override
        public TProtocol getProtocol(TTransport tTransport) {
            return new org.apache.thrift.protocol.TSimpleJSONProtocol(tTransport);
        }
    };
    public abstract TProtocol getProtocol(TTransport tTransport);
}
