package com.pressir.printer;

import com.pressir.ThriftBenchmarkProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Only logs in this file could be printed in console.
 */
public class ConsolePrinter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsolePrinter.class);
    private static String shellName = "BT.sh";

    public static void sayHello() {
        LOGGER.info("This is {}, version {}", ThriftBenchmarkProperties.getProjectName(), ThriftBenchmarkProperties.getProjectVersion());
    }

    public static void sayGoodbye() {
        LOGGER.info("Thank you for using {}, Bye!", ThriftBenchmarkProperties.getProjectName());
    }

    public static void say(String msg, Object... vars) {
        LOGGER.info(msg, vars);
        LOGGER.info("\n");
    }

    public static void onParamError(String message) {
        LOGGER.error("Error: {}", message);
        printUsage();
        printExamples();
    }

    public static void onError(String message) {
        LOGGER.error("Error: {}\n", message);
    }

    private static void printUsage() {
        List<String> usage = ThriftBenchmarkProperties.getUsageSimple();
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
