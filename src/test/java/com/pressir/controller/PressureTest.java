package com.pressir.controller;

import org.junit.Assert;
import org.junit.Test;


public class PressureTest {


    @Test(expected = IllegalArgumentException.class)
    public void constructor_test1() {
        new Pressure(0, "1s");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_test2() {
        new Pressure(10, "0s");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_test3() {
        new Pressure(10, "20m");
    }

    @Test
    public void constructor_test4() {
        new Pressure(10, "20second");
    }

    @Test
    public void constructor_test5() {
        new Pressure(10, "20");
    }

    @Test
    public void getQuantity_test3() {
        Pressure pressure = new Pressure(10, "100");
        Assert.assertEquals(10, pressure.getCurrentQuantity());
    }

    @Test
    public void getQuantity_test4() throws InterruptedException {
        Pressure pressure = new Pressure(10, "1");
        Thread.sleep(2000);
        Assert.assertEquals(-1, pressure.getCurrentQuantity());
    }

}
