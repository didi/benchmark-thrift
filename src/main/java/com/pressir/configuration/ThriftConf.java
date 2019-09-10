package com.pressir.configuration;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @ClassName ThriftConf
 * @Description 配置文件内容
 * @Author pressir
 * @Date 2019-08-30 16:31
 */

public class ThriftConf {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThriftConf.class);

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
        if (jarObj == null) {
            throw new IllegalArgumentException("Must pointed the jar file");
        }
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
        return transport;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public String getJar() {
        return jar;
    }

    public void setJar(String jar) {
        this.jar = jar;
    }

    public static class Protocol {

        private String type;
        private JSONArray props = new JSONArray();

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public JSONArray getProps() {
            return props;
        }

        public void setProps(JSONArray props) {
            this.props = props;
        }
    }

    public static class Transport {
        private String type;
        private JSONArray props = new JSONArray();

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public JSONArray getProps() {
            return props;
        }

        public void setProps(JSONArray props) {
            this.props = props;
        }
    }
}
