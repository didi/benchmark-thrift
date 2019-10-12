package com.didiglobal.pressir.thrift.load;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

import static com.didiglobal.pressir.thrift.constant.Constants.PATTERN;

/**
 * @ClassName DurationParser
 * @Description 时长转换器
 * @Author pressir
 * @Date 2019-09-16 10:59
 */
public class DurationParser {
    public static int parse(String duration) {
        Matcher m = PATTERN.matcher(duration);
        if (!m.matches() || m.groupCount() < 2) {
            throw new IllegalArgumentException("TimeUnit error! " +
                    "s means second, " +
                    "m means minute, " +
                    "h means hour, " +
                    "d means day!");
        }

        int time = Integer.parseInt(m.group(1));
        String unit = m.group(2);
        switch (unit) {
            case "":
            case "s":
            case "second":
            case "seconds":
                return time;
            case "m":
            case "minute":
            case "minutes":
                return (int) TimeUnit.MINUTES.toSeconds(time);
            case "h":
            case "hour":
            case "hours":
                return (int) TimeUnit.HOURS.toSeconds(time);
            case "d":
            case "day":
            case "days":
                return (int) TimeUnit.DAYS.toSeconds(time);
            default:
                throw new IllegalArgumentException("Duration Format Error");
        }
    }
}
