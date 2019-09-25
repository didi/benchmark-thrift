package com.didiglobal.pressir.thrift.printer;

import com.didiglobal.pressir.thrift.ThriftBenchmarkProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Only logs in this file could be printed in console.
 */
public class ConsolePrinter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsolePrinter.class);

    public static void sayHello() {
        LOGGER.info("This is {}, version {}", ThriftBenchmarkProperties.getProjectName(), ThriftBenchmarkProperties.getProjectVersion());
    }

    public static void sayGoodbye() {
        LOGGER.info("Thank you for using {}, Bye!", ThriftBenchmarkProperties.getProjectName());
    }

    public static void say(String msg, Object... vars) {
        LOGGER.info(msg, vars);
    }

    public static void onParamError(String message) {
        LOGGER.error("Error: {}", message);
        printUsage();
        printExamples();
    }

    public static void onError(String message) {
        LOGGER.error("Error: {}", message);
    }

    private static void printUsage() {
        List<String> usage = ThriftBenchmarkProperties.getUsage();
        for (String s : usage) {
            LOGGER.info(s);
        }
    }

    private static void printExamples() {
        List<String> examples = ThriftBenchmarkProperties.getExamples();
        for (String s : examples) {
            LOGGER.info(s);
        }
    }
}
