package com.pressir.context.bean.transport;

/**
 * @ClassName SocketProps
 * @Description TODO
 * @Author didi
 * @Date 2019-09-17 13:43
 */

public class SocketProps extends AbstractProps {
    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    private int socketTimeout = 0;

    private int connectTimeout = 0;
}
