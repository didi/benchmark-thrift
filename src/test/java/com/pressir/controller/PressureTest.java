package com.pressir.controller;

import org.junit.Assert;
import org.junit.Test;


public class PressureTest {


    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_when_given_wrong_quantity_lte0() {
        new Pressure(0, "1s");
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_when_given_wrong_duration_lte0() {
        new Pressure(10, "0s");
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_when_given_wrong_duration_unit() {
        new Pressure(10, "20m");
    }

    @Test
    public void given_right_params_with_second() {
        new Pressure(10, "20second");
    }

    @Test
    public void given_right_params_without_second() {
        new Pressure(10, "20");
    }

    @Test
    public void should_return_quantity_where_pressure_duration_lt_given() {
        Pressure pressure = new Pressure(10, "100");
        Assert.assertEquals(10, pressure.getCurrentQuantity());
    }

    @Test
    public void should_return_negative1_where_pressure_duration_gt_given() throws InterruptedException {
        Pressure pressure = new Pressure(10, "1");
        Thread.sleep(2000);
        Assert.assertEquals(-1, pressure.getCurrentQuantity());
    }

}
