package com.pressir.context;

import org.junit.Test;

import java.io.File;

public class InvocationContextTest {

    @Test
    public void should_return_TaskGenerator() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        File thriftConf = new File(classLoader.getResource("thrift.properties").getFile());
        File argsData = new File(classLoader.getResource("data.text").getFile());
        String uri = "127.0.0.1:8090/Soda/getInfos";
        InvocationContext conf = new InvocationContext(thriftConf, argsData, uri);
        conf.getTaskGenerator();
    }
}
