package com.pressir;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import com.pressir.constant.Constants;
import com.pressir.context.InvocationContext;
import com.pressir.executor.PressureExecutor;
import com.pressir.generator.Generator;
import com.pressir.load.Pressure;
import com.pressir.monitor.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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

    @Parameter(names = {"-p"}, description = "thrift conf", required = true, converter = FileConverter.class)
    private File contextFile;

    @Parameter(names = {"-d"}, description = "params conf", converter = FileConverter.class)
    private File paramsFile;

    @Parameter(names = {"-u"}, description = "url", required = true)
    private String url;

    public static void main(String... args) {
        Main main = new Main();
        JCommander.newBuilder().addObject(main).build().parse(args);
        try {
            main.run();
            TimeUnit.SECONDS.sleep(3);
            main.stop();
            LOGGER.info("Thank you for using Benchmark-Thrift! Bye!");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            System.exit(1);
        }
    }


    private void stop() {
        Monitor.onStop();
    }

    private void run() {

        InvocationContext invocationContext = new InvocationContext(contextFile, paramsFile, url);
        //prepare monitor
        int interval = 1000;
        if (this.threadNum == null) {
            interval = this.throughput > 100 ? 500 : 200;
        }
        Monitor.init(invocationContext.getMethod(), interval);

        //prepare executor
        try (PressureExecutor pressureExecutor = this.getExecutor(invocationContext.getTaskGenerator())) {
            LOGGER.info("This is Thrift-Pressure-Tool");
            LOGGER.info("Server Hostname: {}", invocationContext.getEndpoint().getHost());
            LOGGER.info("Server Port: {}", invocationContext.getEndpoint().getPort());
            LOGGER.info("Pressure Service: {}", invocationContext.getService());
            LOGGER.info("Pressure Method: {}", invocationContext.getMethod());
            LOGGER.info("Pressure Type: {}", this.threadNum == null ? Constants.THROUGHPUT : Constants.CONCURRENCY);
            LOGGER.info("Pressure: {}", this.threadNum == null ? this.throughput : this.threadNum);
            LOGGER.info("Pressure Duration: {}", this.duration);
            LOGGER.info("Benchmarking {} {}/{}", invocationContext.getEndpoint(), invocationContext.getService(), invocationContext.getMethod());
            pressureExecutor.start(1);
        }
    }

    private PressureExecutor getExecutor(Generator generator) {
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
}
