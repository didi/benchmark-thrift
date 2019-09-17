package com.pressir;

import com.pressir.client.RequestTest;
import com.pressir.client.TProtocolFactoryTest;
import com.pressir.configuration.ThriftConfTest;
import com.pressir.controller.DurationParserTest;
import com.pressir.controller.PressureTest;
import com.pressir.utils.ClassCastUtilsTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @ClassName Test
 * @Description TODO
 * @Author didi
 * @Date 2019-09-09 15:36
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        DurationParserTest.class,
        RequestTest.class,
        TProtocolFactoryTest.class,
        ThriftConfTest.class,
        PressureTest.class,
        ClassCastUtilsTest.class,
        MainTest.class})
public class Test {}
