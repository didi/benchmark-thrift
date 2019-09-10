package com.pressir.configuration;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

public class ThriftConfTest {

    private File file;

    @Before
    public void getJarFile() {
        file = new File(System.getProperty("user.dir") + "/src/test/resources/thrift.yml");
    }

    @Test
    public void parse() throws IOException {
        Yaml yaml = new Yaml();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            Map<String, Object> map = yaml.load(fileInputStream);
            map.remove("protocol");
            map.remove("transport");
            ThriftConf thriftConf = ThriftConf.parse(map);
            Assert.assertEquals("TSocket", thriftConf.getTransport().getType());
            Assert.assertEquals("TBinaryProtocol", thriftConf.getProtocol().getType());
        }
    }
}
