package com.pressir;

import com.pressir.idl.service.Service;
import org.apache.thrift.transport.TTransportException;
import org.junit.Before;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;


public class MainTest {

    private String path = Objects.requireNonNull(this.getClass().getClassLoader().getResource("")).getPath();
    private String thriftConf = path + "thrift.yml";
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
        Yaml yaml = new Yaml();
        try (FileInputStream fileInputStream = new FileInputStream(thriftConf)) {
            Map<String, Object> map = yaml.load(fileInputStream);
            map.put("jar", path + "base-1.0-SNAPSHOT.jar");
            yaml.dump(map, new FileWriter(thriftConf));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    @Test(expected = ParameterException.class)
//    public void should_throw_exception_when_no_Duration_given() throws InterruptedException {
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
    public void should_running_when_given_right_params_on_throughtput() {
        Main.main("-q", "100", "-D", "20s", "-p", thriftConf, "-d", paramConf, "-u", "127.0.0.1:8090/Soda/getInfos");
    }

    @org.junit.Test
    public void should_running_when_given_right_params_on_concurrency() {
        Main.main("-c", "2", "-D", "20s", "-p", thriftConf, "-d", paramConf, "-u", "127.0.0.1:8090/Soda/getInfos");
    }
}
