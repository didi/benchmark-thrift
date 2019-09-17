package com.pressir.context.bean.transport;

import com.pressir.context.TransportContext;

/**
 * @ClassName FramedTransportProps
 * @Description TODO
 * @Author didi
 * @Date 2019-09-17 13:42
 */
public class FramedTransportProps extends AbstractProps {
    private TransportContext transport;

    private int maxLength = 16384000;

    public TransportContext getTransport() {
        return transport;
    }

    public void setTransport(TransportContext transport) {
        this.transport = transport;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
}
