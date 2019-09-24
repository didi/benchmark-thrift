package com.pressir;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import com.pressir.constant.Constants;
import com.pressir.context.InvocationContext;
import com.pressir.executor.PressureExecutor;
import com.pressir.generator.Generator;
import com.pressir.load.DurationParser;
import com.pressir.load.Pressure;
import com.pressir.monitor.Monitor;
import com.pressir.printer.ConsolePrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


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
            beforeRun();
            main.run();
            afterRun();

            beforeStop();
            main.stop();
            afterStopped();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            System.exit(1);
        }
    }

    private static void beforeRun() {
        ConsolePrinter.sayHello();
    }

    private static void afterRun() {
    }

    private static void beforeStop() throws InterruptedException {
        int waitInSeconds = 5;
        ConsolePrinter.sayAndWait("Collecting information, please wait", waitInSeconds);
    }

    private static void afterStopped() {
        ConsolePrinter.sayGoodbye();
    }


    private void stop() {
        Monitor.onStop();
    }

    private void run() {

        InvocationContext invocationContext = new InvocationContext(contextFile, paramsFile, url);
        //prepare monitor
        Monitor.init(invocationContext.getMethod(), DurationParser.parse(this.duration) / 10);

        //prepare executor
        try (PressureExecutor pressureExecutor = this.getExecutor(invocationContext.getTaskGenerator())) {
            ConsolePrinter.say("Server Hostname: {}", invocationContext.getEndpoint().getHost());
            ConsolePrinter.say("Server Port: {}", invocationContext.getEndpoint().getPort());
            ConsolePrinter.say("Thrift Service: {}", invocationContext.getService());
            ConsolePrinter.say("Thrift Method: {}", invocationContext.getMethod());
            ConsolePrinter.say("Type: {}", this.threadNum == null ? this.throughput + " " + Constants.THROUGHPUT : this.threadNum + " " + Constants.CONCURRENCY);
            ConsolePrinter.say("Duration {}", this.duration);
            ConsolePrinter.say("Benchmarking {}/{}/{}", invocationContext.getEndpoint(), invocationContext.getService(), invocationContext.getMethod());
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
