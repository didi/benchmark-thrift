package com.pressir.load;

import com.pressir.load.DurationParser;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class DurationParserTest {

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception() {
        DurationParser.parse("s");
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_when_given_error_format() {
        DurationParser.parse("abc10s");
    }

    @Test
    public void should_return_9() {
        Assert.assertEquals(9, DurationParser.parse("9"));
    }

    @Test
    public void should_return_10() {
        Assert.assertEquals(10, DurationParser.parse("10s"));
    }

    @Test
    public void should_return_11() {
        Assert.assertEquals(11, DurationParser.parse("11second"));
    }

    @Test
    public void should_return_12() {
        Assert.assertEquals(12, DurationParser.parse("12seconds"));
    }

    @Test
    public void should_return_60() {
        Assert.assertEquals(60, DurationParser.parse("1m"));
    }

    @Test
    public void should_return_120() {
        Assert.assertEquals(120, DurationParser.parse("2minute"));
    }

    @Test
    public void should_return_180() {
        Assert.assertEquals(180, DurationParser.parse("3minutes"));
    }

    @Test
    public void should_return_3600() {
        Assert.assertEquals(3600, DurationParser.parse("1h"));
    }

    @Test
    public void should_return_7200() {
        Assert.assertEquals(7200, DurationParser.parse("2hour"));
    }

    @Test
    public void should_return_10800() {
        Assert.assertEquals(10800, DurationParser.parse("3hours"));
    }

    @Test
    public void should_return_10800_multi_24() {
        Assert.assertEquals(3600 * 24, DurationParser.parse("1d"));
    }

    @Test
    public void should_return_10800_multi_48() {
        Assert.assertEquals(3600 * 48, DurationParser.parse("2day"));
    }

    @Test
    public void should_return_10800_multi_72() {
        Assert.assertEquals(3600 * 72, DurationParser.parse("3days"));
    }

    /**
     * perf test
     */
    @Test
    public void perf_should_parse_1m_within_2_seconds() {
        long start = System.currentTimeMillis();
        int count = 1000000;
        for (int i = 0; i < count; i++) {
            DurationParser.parse(i + "hours");
        }
        long timeSpent = System.currentTimeMillis() - start;
        assertTrue("expects <2000ms, actual is " + timeSpent + "ms", timeSpent < 2000);
    }
}
