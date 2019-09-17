package com.pressir.context;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.protocol.TSimpleJSONProtocol;
import org.apache.thrift.protocol.TTupleProtocol;

/**
 * @ClassName ProtocolContext
 * @Description TODO
 * @Author didi
 * @Date 2019-09-17 13:46
 */
public class ProtocolContext {
    private String type;

    public void setType(String type) {
        this.type = type;
    }

    TProtocolFactory getFactory() {
        switch (this.type) {
            case "TBinaryProtocol":
                return new TBinaryProtocol.Factory();
            case "TCompactProtocol":
                return new TCompactProtocol.Factory();
            case "TJSONProtocol":
                return new TJSONProtocol.Factory();
            case "TSimpleJSONProtocol":
                return new TSimpleJSONProtocol.Factory();
            case "TTupleProtocol":
                return new TTupleProtocol.Factory();
            default:
                throw new IllegalArgumentException("Protocol type error!");
        }
    }
}
