package com.pressir.controller;

import org.junit.Assert;
import org.junit.Test;


public class PressureTest {

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
