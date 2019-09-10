package com.pressir.utils;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * @ClassName ClientUtils
 * @Description TODO
 * @Author didi
 * @Date 2019-09-03 15:52
 */
public class ClientUtils {
    public static <T extends TServiceClient> void open(T client) throws TTransportException {
        TProtocol iprot = client.getInputProtocol();
        TTransport itrans = iprot.getTransport();
        if (!itrans.isOpen()) {
            itrans.open();
        }
        TProtocol oprot = client.getOutputProtocol();
        if (oprot == iprot) {
            return;
        }
        TTransport otrans = oprot.getTransport();
        if (otrans == itrans) {
            return;
        }
        if (!otrans.isOpen()) {
            otrans.open();
        }
    }

    public static <T extends TServiceClient> void close(T client) {
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
        } finally {
            itrans.close();
        }
    }

    public static <T extends TServiceClient> boolean isOpen(T client) {
        TProtocol iprot = client.getInputProtocol();
        TTransport itrans = iprot.getTransport();
        if (!itrans.isOpen()) {
            return false;
        }
        TProtocol oprot = client.getOutputProtocol();
        if (oprot == iprot) {
            return true;
        }
        TTransport otrans = oprot.getTransport();
        if (otrans == itrans) {
            return true;
        }
        return otrans.isOpen();
    }
}
