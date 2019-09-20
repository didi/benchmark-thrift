package com.pressir.constant;

import java.util.regex.Pattern;

/**
 * @ClassName Constants
 * @Description
 * @Author pressir
 * @Date 2019-08-30 16:18
 */
public class Constants {

    public static final int URI_PARTS = 3;

    public static final String THROUGHPUT = "Throughput";

    public static final String CONCURRENCY = "Concurrency";

    public static final int TIME_CONVERT_BASE = 1000;

    public static final String T_SOCKET = "TSocket";

    public static final String T_FRAMED_TRANSPORT = "TFramedTransport";

    public static final String FACTORY = "Factory";

    public static final Pattern PATTERN = Pattern.compile("(\\d+)(((s|second|seconds)?)|((m|minute|minutes)?)|((h|hour|hours)?)|((d|day|days)?))");

}
