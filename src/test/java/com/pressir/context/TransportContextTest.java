package com.pressir.context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pressir.client.TransportFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class TransportContextTest {

    TransportContext transportContext;
    private File file;
    private Map<String, Object> map;

    @Before
    public void before() {
        file = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("")).getPath() + "thrift.yml");
        Yaml yaml = new Yaml();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            map = yaml.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void should_return_FramedTransportFactory() {
        transportContext = JSONObject.parseObject(JSON.toJSONString(map.get("transport")), TransportContext.class);
        Assert.assertEquals(TransportFactory.FramedTransportFactory.class,transportContext.getFactory().getClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception() {
        transportContext = JSONObject.parseObject(JSON.toJSONString((map.get("transport"))), TransportContext.class);
        transportContext.setType("invalid");
        transportContext.getFactory();
    }
}
