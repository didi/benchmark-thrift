package com.didiglobal.pressir.thrift.load;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class DurationParserTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowException() {
        DurationParser.parse("s");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenGivenErrorFormat() {
        DurationParser.parse("abc10s");
    }

    @Test
    public void shouldReturn9() {
        Assert.assertEquals(9, DurationParser.parse("9"));
    }

    @Test
    public void shouldReturn10() {
        Assert.assertEquals(10, DurationParser.parse("10s"));
    }

    @Test
    public void shouldReturn11() {
        Assert.assertEquals(11, DurationParser.parse("11second"));
    }

    @Test
    public void shouldReturn12() {
        Assert.assertEquals(12, DurationParser.parse("12seconds"));
    }

    @Test
    public void shouldReturn60() {
        Assert.assertEquals(60, DurationParser.parse("1m"));
    }

    @Test
    public void shouldReturn120() {
        Assert.assertEquals(120, DurationParser.parse("2minute"));
    }

    @Test
    public void shouldReturn180() {
        Assert.assertEquals(180, DurationParser.parse("3minutes"));
    }

    @Test
    public void shouldReturn3600() {
        Assert.assertEquals(3600, DurationParser.parse("1h"));
    }

    @Test
    public void shouldReturn7200() {
        Assert.assertEquals(7200, DurationParser.parse("2hour"));
    }

    @Test
    public void shouldReturn10800() {
        Assert.assertEquals(10800, DurationParser.parse("3hours"));
    }

    @Test
    public void shouldReturn10800Multi24() {
        Assert.assertEquals(3600 * 24, DurationParser.parse("1d"));
    }

    @Test
    public void shouldReturn10800Multi48() {
        Assert.assertEquals(3600 * 48, DurationParser.parse("2day"));
    }

    @Test
    public void shouldReturn10800Multi72() {
        Assert.assertEquals(3600 * 72, DurationParser.parse("3days"));
    }

    /**
     * perf test
     */
    @Test
    public void perfShouldParse1mWithin3Seconds() {
        long start = System.currentTimeMillis();
        int count = 1000000;
        for (int i = 0; i < count; i++) {
            DurationParser.parse(i + "hours");
        }
        long timeSpent = System.currentTimeMillis() - start;
        assertTrue("expects <3000ms, actual is " + timeSpent + "ms", timeSpent < 3000);
    }
}
