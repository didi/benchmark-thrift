package com.pressir.context;

import com.alibaba.fastjson.JSONObject;
import com.pressir.client.TransportFactory;
import com.pressir.context.bean.transport.AbstractProps;
import com.pressir.context.bean.transport.FramedTransportProps;
import com.pressir.context.bean.transport.SocketProps;

import static com.pressir.constant.Constants.T_FRAMED_TRANSPORT;
import static com.pressir.constant.Constants.T_SOCKET;

/**
 * @ClassName TransportContext
 * @Description TODO
 * @Author didi
 * @Date 2019-09-17 13:37
 */
public class TransportContext {

    private String type;

    private AbstractProps props;

    public void setType(String type) {
        this.type = type;
    }

    public void setProps(JSONObject props) {
        switch (this.type) {
            case T_SOCKET:
                this.props = JSONObject.parseObject(props.toJSONString(), SocketProps.class);
                break;
            case T_FRAMED_TRANSPORT:
                this.props = JSONObject.parseObject(props.toJSONString(), FramedTransportProps.class);
                break;
            default:
                throw new IllegalArgumentException("Tool only support TSocket and TFramedTransport!");
        }
    }

    TransportFactory getFactory() {
        switch (this.type) {
            case T_SOCKET:
                if (this.props == null) {
                    return new TransportFactory.SocketFactory();
                }
                SocketProps socketProps = (SocketProps) this.props;
                return new TransportFactory.SocketFactory(socketProps.getSocketTimeout(), socketProps.getConnectTimeout());
            case T_FRAMED_TRANSPORT:
                if (this.props == null) {
                    return new TransportFactory.FramedTransportFactory();
                }
                FramedTransportProps framedTransportProps = (FramedTransportProps) this.props;
                return new TransportFactory.FramedTransportFactory(framedTransportProps.getTransport().getFactory(), framedTransportProps.getMaxLength());
            default:
                throw new IllegalArgumentException("Tool only support TSocket and TFramedTransport!");
        }
    }
}
