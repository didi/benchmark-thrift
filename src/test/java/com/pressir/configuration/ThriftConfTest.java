package com.pressir.configuration;


import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class ThriftConfTest {

    private File file;

    @Before
    public void before() {
        file = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("")).getPath() + "thrift.yml");
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_when_protocol_absent() throws IOException {
        Yaml yaml = new Yaml();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            Map<String, Object> map = yaml.load(fileInputStream);
            map.remove("protocol");
            ThriftConf.parse(map);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_when_transport_absent() throws IOException {
        Yaml yaml = new Yaml();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            Map<String, Object> map = yaml.load(fileInputStream);
            map.remove("transport");
            ThriftConf.parse(map);
        }
    }
}
