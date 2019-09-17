package com.pressir.context;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class ThriftContextTest {

    private Map<String, Object> map;
    private File file;

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

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_when_protocol_absent() throws IOException {
        map.remove("protocol");
        ThriftContext.parse(map);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_when_transport_absent() throws IOException {
        map.remove("transport");
        ThriftContext.parse(map);
    }

    @Test
    public void should_return_jar_file_path() {
        Assert.assertEquals("base-1.0-SNAPSHOT.jar", ThriftContext.parse(map).getJar());
    }
}
