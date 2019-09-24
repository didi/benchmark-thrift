package com.pressir.printer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Only logs in this file could be printed in console.
 */
public class ConsolePrinter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsolePrinter.class);
    private static String projectName = "benchmark-thrift tool";
    private static String version = "1.0.0";

    public static void sayHello() {
        LOGGER.info("This is {}, version {}", projectName, version);
    }

    public static void sayGoodbye() {
        LOGGER.info("Thank you for using {}, Bye!", projectName);
    }

    public static void say(String msg, Object... vars) {
        LOGGER.info(msg, vars);
    }
}
