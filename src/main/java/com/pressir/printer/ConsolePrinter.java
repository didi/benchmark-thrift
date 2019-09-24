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
    private static String shellName = "BT.sh";

    public static void sayHello() {
        LOGGER.info("This is {}, version {}\n", projectName, version);
    }

    public static void sayGoodbye() {
        LOGGER.info("Thank you for using {}, Bye!\n", projectName);
    }

    public static void say(String msg, Object... vars) {
        LOGGER.info(msg, vars);
        LOGGER.info("\n");
    }

    public static void onParamError(String message) {
        LOGGER.error("Error: {}\n", message);
        printUsage();
        printExamples();
    }

    public static void onError(String message) {
        LOGGER.error("Error: {}\n", message);
    }

    private static void printUsage() {
        LOGGER.info("Usage: \n");
        LOGGER.info("   ./{} -p <protocol file> -d <data file> [ -c concurrency ] [ -D duration ] [options] <host>:<port>/<service>/<method>\n", shellName);
    }

    private static void printExamples() {
        LOGGER.info("Examples: \n");
        LOGGER.info("   #Benchmark at 10 concurrencies for 60 seconds:\n");
        LOGGER.info("   ./{} -p protocol.conf -d data.txt -c 10 -D 60s 127.0.0.1:8090/service/method\n", shellName);
    }
}
