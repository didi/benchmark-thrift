package com.didiglobal.pressir.thrift.printer;

import com.didiglobal.pressir.thrift.BenchmarkThriftProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Only logs in this file could be printed in console.
 */
public class ConsolePrinter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsolePrinter.class);

    public static void sayHello() {
        LOGGER.info("This is {}, version {}", BenchmarkThriftProperties.getProjectName(), BenchmarkThriftProperties.getProjectVersion());
    }

    public static void sayGoodbye() {
        LOGGER.info("Thank you for using {}, Bye!", BenchmarkThriftProperties.getProjectName());
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
        List<String> usage = BenchmarkThriftProperties.getUsage();
        for (String s : usage) {
            LOGGER.info(s);
        }
    }

    private static void printExamples() {
        List<String> examples = BenchmarkThriftProperties.getExamples();
        for (String s : examples) {
            LOGGER.info(s);
        }
    }
}
