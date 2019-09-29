package com.didiglobal.pressir.thrift.context;

import org.junit.Test;

import java.io.File;

public class InvocationContextTest {

    @Test
    public void shouldReturnTaskGenerator() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        File thriftConf = new File(classLoader.getResource("thrift.conf").getFile());
        String uri = "thrift://127.0.0.1:8090/Soda/getInfos?{\"shopId\":\"11\",\"status\":\"OPEN\"}";
        InvocationContext conf = new InvocationContext(thriftConf, uri);
        conf.getTaskGenerator();
    }
}
