package com.didiglobal.pressir.thrift.load;

import org.junit.Assert;
import org.junit.Test;


public class PressureTest {

    @Test
    public void shouldReturnQuantityWherePressureDurationLtGiven() {
        Pressure pressure = new Pressure(10, "100");
        Assert.assertEquals(10, pressure.getCurrentQuantity());
    }

    @Test
    public void shouldReturnNegativeWherePressureDurationGtGiven() throws InterruptedException {
        Pressure pressure = new Pressure(10, "1");
        Thread.sleep(2000);
        Assert.assertEquals(-1, pressure.getCurrentQuantity());
    }

}
