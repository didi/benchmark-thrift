package com.pressir.controller;

import com.pressir.constant.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName Pressure
 * @Description 压力曲线
 * @Author pressir
 * @Date 2019-08-30 16:34
 */
public class Pressure {

    private int quantity;

    private int duration;

    private long beginTime;

    public Pressure(int quantity, String duration) {
        this(quantity, duration, System.currentTimeMillis() / Constants.TIME_CONVERT_BASE + 1);
    }

    private Pressure(int quantity, String durationString, long beginTime) {
        validate(quantity, durationString);
        this.beginTime = beginTime;
    }

    private void validate(int quantity, String durationString) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("The pressure should be gt 0");
        }
        this.quantity = quantity;
        try {
            this.duration = Integer.parseInt(durationString);
            return;
        } catch (NumberFormatException ignored) {

        }
        String pattern = "(\\d+)s(econd)?";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(durationString);
        if (m.matches()) {
            this.duration = Integer.parseInt(m.group(1));
        }
        if (this.duration <= 0) {
            throw new IllegalArgumentException("The duration should be gt 0");
        }
    }


    public int getCurrentQuantity() {
        if (System.currentTimeMillis() / Constants.TIME_CONVERT_BASE - beginTime >= duration) {
            return -1;
        }
        return quantity;
    }

}
