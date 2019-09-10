package com.pressir;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.IStringConverterFactory;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import com.google.common.io.Files;
import com.google.common.net.HostAndPort;
import com.pressir.client.BaseClientFactory;
import com.pressir.client.DefaultClientFactory;
import com.pressir.client.Request;
import com.pressir.client.TProtocolFactory;
import com.pressir.client.TTransportFactory;
import com.pressir.configuration.ThriftConf;
import com.pressir.constant.Constants;
import com.pressir.controller.Pressure;
import com.pressir.executor.PressureExecutor;
import com.pressir.generator.Generator;
import com.pressir.generator.InvariantTaskGenerator;
import com.pressir.monitor.Monitor;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @ClassName Main
 * @Description main
 * @Author pressir
 * @Date 2019-08-30 15:57
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    @Parameter(names = {"-c"}, description = "concurrency")
    private Integer threadNum;

    @Parameter(names = {"-D"}, description = "duration", required = true)
    private String duration;

    @Parameter(names = {"-q"}, description = "throughput")
    private Integer throughput;

    @Parameter(names = {"-n"}, description = "requests")
    private Integer requests;

    @Parameter(names = {"-p"}, description = "thrift conf", required = true, converter = FileConverter.class)
    private File thriftConfFile;

    @Parameter(names = {"-d"}, description = "params conf", required = true, converter = FileConverter.class)
    private File paramsConfFile;

    @Parameter(names = {"-u"}, description = "url", required = true, converter = UrlConverter.class)
    private Url url;

    private ThriftConf thriftConf;

    public static void main(String... args) {
        Main main = new Main();
        JCommander.newBuilder().addObject(main).addConverterFactory(new ConverterFactory()).build().parse(args);
        try {
            main.run();
            TimeUnit.SECONDS.sleep(3);
            main.stop();
            LOGGER.info("Pressure finished! Thanks! Bye!");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage() + ":" + e);
        } finally {
            System.exit(1);
        }
    }

    private static ThriftConf yaml2JavaBean(File file) throws IOException {
        Map<String, Object> loaded;
        try (FileInputStream fis = new FileInputStream(file)) {
            Yaml yaml = new Yaml();
            loaded = yaml.load(fis);
        }
        return ThriftConf.parse(loaded);
    }

    private void stop() {
        Monitor.onStop();
    }

    private void run() throws Exception {
        //Determine file format
        if (this.thriftConfFile.getName().endsWith(Constants.YML)) {
            this.thriftConf = yaml2JavaBean(this.thriftConfFile);
        } else {
            throw new IllegalArgumentException("File format is error!");
        }

        //validate for pressure type
        if (this.threadNum == null && this.throughput == null) {
            throw new IllegalArgumentException("Must pointed the pressure type!");
        }

        if (this.threadNum != null && this.throughput != null) {
            throw new IllegalArgumentException("Only one pressure type can be pointed!");
        }

        //prepare monitor
        int interval = 1000;
        if (this.threadNum == null) {
            interval = this.throughput > 100 ? 500 : 200;
        }
        Monitor.init(this.url.service + "." + this.url.method, interval);

        //prepare executor
        try (PressureExecutor pressureExecutor = this.getExecutor()) {
            LOGGER.info("This is Thrift-Pressure-Tool");
            LOGGER.info("Server Hostname: {}", this.url.hostAndPort.getHost());
            LOGGER.info("Server Port: {}", this.url.hostAndPort.getPort());
            LOGGER.info("Pressure Service: {}", this.url.service);
            LOGGER.info("Pressure Method: {}", this.url.method);
            LOGGER.info("Pressure Type: {}", this.threadNum == null ? Constants.THROUGHPUT : Constants.CONCURRENCY);
            LOGGER.info("Pressure: {}", this.threadNum == null ? throughput : threadNum);
            LOGGER.info("Pressure duration: {}", duration);
            LOGGER.info("Benchmarking {} {}/{}", this.url.hostAndPort, this.url.service, this.url.method);
            pressureExecutor.start(1);
        }
    }

    private PressureExecutor getExecutor() throws Exception {
        //prepare generator
        Generator generator = this.getGenerator();
        //prepare pressure
        Pressure pressure = this.getPressure();

        if (this.threadNum != null) {
            return PressureExecutor.concurrency(generator, pressure::getCurrentQuantity);
        } else {
            return PressureExecutor.throughput(generator, pressure::getCurrentQuantity);
        }
    }

    private Pressure getPressure() {
        Integer quantity = this.threadNum == null ? this.throughput : this.threadNum;
        return new Pressure(quantity, this.duration);
    }

    private <T extends TServiceClient> Generator getGenerator() throws Exception {
        Request request = Request.parseRequest(this.url.service, this.url.method, Files.readLines(this.paramsConfFile, StandardCharsets.UTF_8), new File(this.thriftConf.getJar()));
        BaseClientFactory<T> clientFactory = this.getClientFactory(request.getInnerFactory());
        return InvariantTaskGenerator.newInstance(clientFactory, request);
    }

    private <T extends TServiceClient> BaseClientFactory getClientFactory(TServiceClientFactory<T> serviceClientFactory) throws Exception {
        TProtocolFactory protocolFactory = TProtocolFactory.valueOf(this.thriftConf.getProtocol().getType());
        TTransportFactory transportFactory = getTTransportFactory();
        return new DefaultClientFactory<>(serviceClientFactory, protocolFactory, transportFactory);
    }

    private TTransportFactory getTTransportFactory() throws InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException, NoSuchMethodException {
        Class<?>[] innerClasses = TTransportFactory.class.getClasses();
        String factoryName = this.thriftConf.getTransport().getType() + Constants.FACTORY;
        TTransportFactory transportFactory = null;
        for (Class innerClass : innerClasses) {
            if (innerClass.getSimpleName().equals(factoryName)) {
                transportFactory = (TTransportFactory) innerClass.getDeclaredConstructor(HostAndPort.class).newInstance(this.url.hostAndPort);
                break;
            }
        }
        return transportFactory;
    }

    private static class Url {
        private final HostAndPort hostAndPort;
        private final String service;
        private final String method;

        private Url(HostAndPort hostAndPort, String service, String method) {
            this.hostAndPort = hostAndPort;
            this.service = service;
            this.method = method;
        }

        static Url parse(String url) {
            String[] parts = url.split("/");
            if (validate(parts)) {
                return new Url(HostAndPort.fromString(parts[0]), parts[1], parts[2]);
            }
            throw new IllegalArgumentException("The format of Url is wrong! It should be Host:Port/Service/Method!");
        }

        private static boolean validate(String[] parts) {
            return parts.length == Constants.URL_PARTS;
        }
    }

    public static class ConverterFactory implements IStringConverterFactory {
        @Override
        public Class<? extends IStringConverter<?>> getConverter(Class forType) {
            if (forType.equals(Url.class)) {
                return UrlConverter.class;
            } else {
                return null;
            }
        }
    }

    private static class UrlConverter implements IStringConverter<Url> {
        @Override
        public Url convert(String url) {
            return Url.parse(url);
        }
    }
}
