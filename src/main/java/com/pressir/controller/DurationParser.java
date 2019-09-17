package com.pressir.controller;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.pressir.constant.Constants.MIN_GROUP_COUNT;

/**
 * @ClassName DurationParser
 * @Description TODO
 * @Author didi
 * @Date 2019-09-16 10:59
 */
class DurationParser {

    static int parse(String duration) {
        String pattern = "(\\d+)(((s|second|seconds)?)|((m|minute|minutes)?)|((h|hour|hours)?)|((d|day|days)?))";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(duration);
        if (m.matches()) {
            int time = Integer.parseInt(m.group(1));
            if (m.groupCount() >= MIN_GROUP_COUNT) {
                switch (m.group(2)) {
                    case "":
                    case "s":
                    case "second":
                    case "seconds":
                        return time;
                    case "m":
                    case "minute":
                    case "minutes":
                        return TimeParser.MINUTE.function.apply(time);
                    case "h":
                    case "hour":
                    case "hours":
                        return TimeParser.HOUR.function.apply(time);
                    case "d":
                    case "day":
                    case "days":
                        return TimeParser.DAY.function.apply(time);
                    default:
                        throw new IllegalArgumentException("Duration format error!");
                }
            }
        }
        throw new IllegalArgumentException("Duration format error!");
    }

    public enum TimeParser {
        MINUTE((duration) -> (int) TimeUnit.MINUTES.toSeconds(duration)),
        HOUR((duration) -> (int) TimeUnit.HOURS.toSeconds(duration)),
        DAY((duration) -> (int) TimeUnit.DAYS.toSeconds(duration));
        private Function<Integer, Integer> function;
        TimeParser(Function<Integer, Integer> function) {
            this.function = function;
        }
    }
}
