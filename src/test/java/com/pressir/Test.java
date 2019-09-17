package com.pressir;

import com.pressir.client.OneHostAndPortTest;
import com.pressir.client.RequestTest;
import com.pressir.context.ProtocolContextTest;
import com.pressir.context.ThriftContextTest;
import com.pressir.context.TransportContextTest;
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
        ThriftContextTest.class,
        OneHostAndPortTest.class,
        TransportContextTest.class,
        DurationParserTest.class,
        ProtocolContextTest.class,
        RequestTest.class,
        PressureTest.class,
        ClassCastUtilsTest.class,
        MainTest.class})
public class Test {}
