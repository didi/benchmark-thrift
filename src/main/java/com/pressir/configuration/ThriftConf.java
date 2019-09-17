package com.pressir.configuration;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.net.HostAndPort;
import com.pressir.client.TTransportFactory;

import java.util.Map;

import static com.pressir.constant.Constants.T_FRAMED_TRANSPORT;
import static com.pressir.constant.Constants.T_SOCKET;

/**
 * @ClassName ThriftConf
 * @Description 配置文件内容
 * @Author pressir
 * @Date 2019-08-30 16:31
 */

public class ThriftConf {

    private Transport transport;

    private Protocol protocol;

    private String jar;

    private ThriftConf(String jar, Protocol protocol, Transport transport) {
        this.jar = jar;
        this.transport = transport;
        this.protocol = protocol;
    }

    public static ThriftConf parse(Map<String, Object> map) {
        Object jarObj = map.get("jar");
        Object protocolObj = map.get("protocol");
        if (protocolObj == null) {
            throw new IllegalArgumentException("Must pointed the protocol");
        }

        Object transportObj = map.get("transport");
        if (transportObj == null) {
            throw new IllegalArgumentException("Must pointed the transport");
        }
        return new ThriftConf((String) jarObj,
                JSON.parseObject(JSON.toJSONString(protocolObj), Protocol.class),
                JSON.parseObject(JSON.toJSONString(transportObj), Transport.class));
    }

    public Transport getTransport() {
        return this.transport;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    public Protocol getProtocol() {
        return this.protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public String getJar() {
        return this.jar;
    }

    public void setJar(String jar) {
        this.jar = jar;
    }


    public static class Protocol {

        private String type;

        public String getType() {
            return this.type;
        }

        public void setType(String type) {
            this.type = type;
        }

    }

    public static class Transport {

        private String type;

        private TransportProps props;

        public String getType() {
            return this.type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public TransportProps getProps() {
            return this.props;
        }

        public void setProps(JSONObject props) {
            switch (this.type) {
                case T_SOCKET:
                    this.props = JSONObject.parseObject(props.toJSONString(), TSocketProps.class);
                    break;
                case T_FRAMED_TRANSPORT:
                    this.props = JSONObject.parseObject(props.toJSONString(), TFramedTransportProps.class);
                    break;
                default:
                    throw new IllegalArgumentException("Tool only support TSocket and TFramedTransport!");
            }
        }

        public TTransportFactory getFactory(HostAndPort hostAndPort) {
            if (this.props == null) {
                switch (this.type) {
                    case T_SOCKET:
                        return new TTransportFactory.TSocketFactory(hostAndPort, 0, 0);
                    case T_FRAMED_TRANSPORT:
                        return new TTransportFactory.TFramedTransportFactory(new TTransportFactory.TSocketFactory(hostAndPort, 0, 0), 16384000);
                    default:
                        throw new IllegalArgumentException("Tool only support TSocket and TFramedTransport!");
                }
            }
            return this.props.getFactory(hostAndPort);
        }

        public static class TSocketProps extends TransportProps {
            int socketTimeout = 0;
            int connectTimeout = 0;

            @Override
            public TTransportFactory getFactory(HostAndPort hostAndPort) {
                return new TTransportFactory.TSocketFactory(hostAndPort, this.socketTimeout, this.connectTimeout);
            }

            public void setSocketTimeout(int socketTimeout) {
                this.socketTimeout = socketTimeout;
            }


            public void setConnectTimeout(int connectTimeout) {
                this.connectTimeout = connectTimeout;
            }
        }

        public static class TFramedTransportProps extends TransportProps {
            Transport transport;
            int maxLength = 16384000;

            @Override
            public TTransportFactory getFactory(HostAndPort hostAndPort) {
                if (this.transport == null) {
                    return new TTransportFactory.TFramedTransportFactory(new TSocketProps().getFactory(hostAndPort), this.maxLength);
                }
                return new TTransportFactory.TFramedTransportFactory(this.transport.getFactory(hostAndPort), this.maxLength);
            }

            public void setTransport(JSONObject transport) {
                this.transport = JSONObject.parseObject(JSON.toJSONString(transport),Transport.class);
            }

            public void setMaxLength(int maxLength) {
                this.maxLength = maxLength;
            }
        }

        public abstract static class TransportProps {
            public abstract TTransportFactory getFactory(HostAndPort hostAndPort);
        }
    }
}
