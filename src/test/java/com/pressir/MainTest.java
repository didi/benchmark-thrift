package com.pressir;

import com.beust.jcommander.ParameterException;
import com.pressir.idl.service.Service;
import org.apache.thrift.transport.TTransportException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;


public class MainTest {
//
//    @Before
//    public void before() {
//        new Thread(() -> {
//            try {
//                Service.run();
//            } catch (TTransportException e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }

//    @Test(expected = ParameterException.class)
//    public void main_test_no_D() throws InterruptedException {
//        TimeUnit.SECONDS.sleep(1);
//        String thriftConf = System.getProperty("user.dir") + "/src/test/resources/thrift.yml";
//        String paramConf = System.getProperty("user.dir") + "/src/test/resources/data.text";
//        Main.main("-p", thriftConf, "-d", paramConf, "-u", "127.0.0.1:8090/Soda/getInfos");
//    }
//
//    @Test
//    public void main_test_no_cOrq_exit() {
//        String thriftConf = System.getProperty("user.dir") + "/src/test/resources/thrift.yml";
//        String paramConf = System.getProperty("user.dir") + "/src/test/resources/data.text";
//        Main.main("-D", "10", "-p", thriftConf, "-d", paramConf, "-u", "127.0.0.1:8090/Soda/getInfos");
//    }
//
//    @Test
//    public void main_test_both_cAndq_exit() {
//        String thriftConf = System.getProperty("user.dir") + "/src/test/resources/thrift.yml";
//        String paramConf = System.getProperty("user.dir") + "/src/test/resources/data.text";
//        Main.main("-q", "100", "-c", "1", "-D", "10", "-p", thriftConf, "-d", paramConf, "-u", "127.0.0.1:8090/Soda/getInfos");
//    }

    @org.junit.Test
    public void main_test_throughtput() throws InterruptedException {
        String thriftConf = System.getProperty("user.dir") + "/src/test/resources/thrift.yml";
        String paramConf = System.getProperty("user.dir") + "/src/test/resources/data.text";
        Main.main("-q", "100", "-D", "20s", "-p", thriftConf, "-d", paramConf, "-u", "127.0.0.1:8090/Soda/getInfos");
    }

    @org.junit.Test
    public void main_test_concurrency() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        String thriftConf = System.getProperty("user.dir") + "/src/test/resources/thrift.yml";
        String paramConf = System.getProperty("user.dir") + "/src/test/resources/data.text";
        Main.main("-c", "2", "-D", "20s", "-p", thriftConf, "-d", paramConf, "-u", "127.0.0.1:8090/Soda/getInfos");
    }
}
