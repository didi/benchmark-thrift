package com.didiglobal.pressir.thrift;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.converters.FileConverter;
import com.didiglobal.pressir.thrift.printer.ConsolePrinter;
import com.didiglobal.pressir.thrift.constant.Constants;
import com.didiglobal.pressir.thrift.context.InvocationContext;
import com.didiglobal.pressir.thrift.executor.PressureExecutor;
import com.didiglobal.pressir.thrift.generator.Generator;
import com.didiglobal.pressir.thrift.load.DurationParser;
import com.didiglobal.pressir.thrift.load.Pressure;
import com.didiglobal.pressir.thrift.monitor.Monitor;
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
        try {
            JCommander.newBuilder().addObject(main).build().parse(args);
        } catch (ParameterException e) {
            ConsolePrinter.onParamError(e.getMessage());
            return;
        }
        try {
            beforeRun();
            main.run();
            afterRun();
            beforeStop();
            main.stop();
            afterStopped();
        } catch (Exception e) {
            ConsolePrinter.onError(e.getMessage());
        }
    }

    private static void beforeRun() {
        ConsolePrinter.sayHello();
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
            ConsolePrinter.say("Duration: {}", this.duration);
            ConsolePrinter.say("Benchmarking {}/{}/{}", invocationContext.getEndpoint(), invocationContext.getService(), invocationContext.getMethod());
            ConsolePrinter.say("\tSend\tSuccess\tTE\tPE\tAE\tOE");
            pressureExecutor.start(1);
        }
    }

    private static void afterRun() {
    }

    private static void beforeStop() throws InterruptedException {
        int waitInSeconds = 5;
        TimeUnit.SECONDS.sleep(waitInSeconds);
    }

    private void stop() {
        Monitor.onStop();
    }

    private static void afterStopped() {
        ConsolePrinter.sayGoodbye();
    }

    private PressureExecutor getExecutor(Generator generator) {
        if (this.threadNum != null) {
            Pressure pressure = new Pressure(threadNum, this.duration);
            return PressureExecutor.concurrency(generator, pressure::getCurrentQuantity);
        }

        Pressure pressure = new Pressure(this.throughput, this.duration);
        return PressureExecutor.throughput(generator, pressure::getCurrentQuantity);
    }
}
