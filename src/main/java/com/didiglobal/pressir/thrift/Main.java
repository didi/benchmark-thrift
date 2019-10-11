package com.didiglobal.pressir.thrift;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.converters.FileConverter;
import com.didiglobal.pressir.thrift.console.ConsolePrinter;
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

    @Parameter(names = {"-t"}, description = "timeLimit", required = true)
    private String timeLimit;

    @Parameter(names = {"-q"}, description = "throughput")
    private Integer throughput;

    @Parameter(names = {"-e"}, description = "thrift env", required = true, converter = FileConverter.class)
    private File thriftEnv;

    @Parameter(names = {"-u"}, description = "url", required = true)
    private String url;

    public static void main(String... args) {
        Main main = new Main();
        try {
            JCommander.newBuilder().addObject(main).build().parse(args);
        } catch (ParameterException e) {
            ConsolePrinter.onError(e.getMessage());
            ConsolePrinter.printUsage();
            return;
        }
        try {
            main.run();
            TimeUnit.SECONDS.sleep(5);
            Monitor.onStop();
            ConsolePrinter.sayGoodbye();
        } catch (Exception e) {
            ConsolePrinter.onError(e.getMessage());
        }
    }

    private void run() {

        InvocationContext invocationContext = new InvocationContext(this.thriftEnv, this.url);

        //prepare executor
        try (PressureExecutor pressureExecutor = this.getExecutor(invocationContext.getTaskGenerator())) {
            //prepare monitor
            Monitor.init(invocationContext.getMethod(), DurationParser.parse(this.timeLimit) / 10);
            ConsolePrinter.say("Server Hostname: {}", invocationContext.getEndpoint().getHost());
            ConsolePrinter.say("Server Port: {}", invocationContext.getEndpoint().getPort());
            ConsolePrinter.say("Thrift Service: {}", invocationContext.getService());
            ConsolePrinter.say("Thrift Method: {}", invocationContext.getMethod());
            ConsolePrinter.say("Type: {}", this.threadNum == null ?
                    this.throughput + " " + Constants.THROUGHPUT :
                    this.threadNum + " " + Constants.CONCURRENCY);
            ConsolePrinter.say("Duration: {}", this.timeLimit);
            ConsolePrinter.say("Benchmarking {}/{}/{}",
                    invocationContext.getEndpoint(),
                    invocationContext.getService(),
                    invocationContext.getMethod());
            ConsolePrinter.say("\nTotal means the number of request prepared to be sent");
            ConsolePrinter.say("Send means the number of request which has been sent");
            ConsolePrinter.say("Success means the number of request which has been responded");
            ConsolePrinter.say("TE means TTransportException");
            ConsolePrinter.say("PE means TProtocolException");
            ConsolePrinter.say("AE means ApplicationException");
            ConsolePrinter.say("OE means Other Exceptions\n");
            ConsolePrinter.say("\tTotal\tSend\tSuccess\tTE\tPE\tAE\tOE");
            pressureExecutor.start(1);
        }
    }

    private PressureExecutor getExecutor(Generator generator) {
        if (this.threadNum != null) {
            Pressure pressure = new Pressure(this.threadNum, this.timeLimit);
            return PressureExecutor.concurrency(generator, pressure::getCurrentQuantity);
        }

        Pressure pressure = new Pressure(this.throughput, this.timeLimit);
        return PressureExecutor.throughput(generator, pressure::getCurrentQuantity);
    }
}
