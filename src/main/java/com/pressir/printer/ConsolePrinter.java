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
        LOGGER.info("This is {}, version {}\n", projectName, version);
    }

    public static void sayGoodbye() {
        LOGGER.info("Thank you for using {}, Bye!\n", projectName);
    }

    public static void say(String msg, Object... vars) {
        LOGGER.info(msg, vars);
        LOGGER.info("\n");
    }

    public static void sayAndWait(String msg, int waitInSeconds) {
        LOGGER.info(msg);
        Runnable runnable = () -> {
            int count = 0;
            while (count++ < waitInSeconds) {
                LOGGER.info(".");
                try {
                    Thread.sleep(1 * 1000L);
                } catch (InterruptedException e) {
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join(waitInSeconds * 1000L);
        } catch (InterruptedException e) {
        }
        LOGGER.info("\n");
    }
}
