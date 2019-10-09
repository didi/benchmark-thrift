package com.didiglobal.pressir.thrift;

import com.pressir.idl.service.Service;
import org.apache.thrift.transport.TTransportException;
import org.junit.Before;

import java.util.Objects;


public class MainTest {

    private String path = Objects.requireNonNull(this.getClass().getClassLoader().getResource("")).getPath();
    private String thriftConf = path + "thrift.conf";
    private String paramConf = path + "data.text";

    @Before
    public void before() {
        new Thread(() -> {
            try {
                Service.run();
            } catch (TTransportException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @org.junit.Test
    public void shouldRunningWhenGivenRightParamsOnThroughput() {
        Main.main("-q", "100", "-t", "20s", "-e", thriftConf, "-u", "127.0.0.1:8090/Soda/getInfos?@"+paramConf);
    }

    @org.junit.Test
    public void shouldRunningWhenGivenRightParamsOnConcurrency() {
        Main.main("-c", "2", "-t", "20s", "-e", thriftConf, "-u", "127.0.0.1:8090/Soda/getInfos?@"+paramConf);
    }
}
