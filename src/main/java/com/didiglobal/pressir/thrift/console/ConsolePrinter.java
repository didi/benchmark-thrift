package com.didiglobal.pressir.thrift.console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Only logs in this file could be printed in console.
 */

/**
 * @author zhangxiaoqing
 */
public class ConsolePrinter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsolePrinter.class);
    private static String name = "BenchmarkThrift";
    private static String shell = "benchmark";

    public static void sayGoodbye() {
        say("Thank you for using {}, Bye!", name);
    }

    public static void say(String msg, Object... vars) {
        LOGGER.info(msg, vars);
    }

    public static void onError(String message, Object... vars) {
        LOGGER.error("{}: " + message, shell, vars);
    }

    public static void printUsage() {
        say("{}: use -h for usages", shell);
    }
}
