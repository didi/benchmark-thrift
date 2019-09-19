package com.pressir.controller;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.pressir.constant.Constants.PATTERN;

/**
 * @ClassName DurationParser
 * @Description TODO
 * @Author didi
 * @Date 2019-09-16 10:59
 */
class DurationParser {


    static int parse(String duration) {
        Matcher m = PATTERN.matcher(duration);
        if (!m.matches() || m.groupCount() < 2) {
            throw new IllegalArgumentException("Duration format error!");
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
                throw new IllegalArgumentException("Duration format error!");
        }
    }
}
