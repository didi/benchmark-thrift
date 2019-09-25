package com.didiglobal.pressir.thrift.console;

import com.didiglobal.pressir.thrift.DeployInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Only logs in this file could be printed in console.
 */
public class ConsolePrinter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsolePrinter.class);

    public static void sayHello() {
        LOGGER.info("This is {}, version {}", DeployInfo.getName(), DeployInfo.getVersion());
    }

    public static void sayGoodbye() {
        LOGGER.info("Thank you for using {}, Bye!", DeployInfo.getName());
    }

    public static void say(String msg, Object... vars) {
        LOGGER.info(msg, vars);
    }

    public static void onParamError(String message) {
        LOGGER.error("{}: {}", DeployInfo.getShell(), message);
        printUsage();
        printExamples();
    }

    public static void onError(String message, Object... vars) {
        LOGGER.error(message, vars);
    }

    private static void printUsage() {
        List<String> usage = DeployInfo.getUsage();
        for (String s : usage) {
            LOGGER.info(s);
        }
    }

    private static void printExamples() {
        List<String> examples = DeployInfo.getExamples();
        for (String s : examples) {
            LOGGER.info(s);
        }
    }
}
