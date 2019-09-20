package com.pressir;

import com.pressir.base.transport.TFramedTransportFactoryTest;
import com.pressir.base.transport.TSocketFactoryTest;
import com.pressir.context.ContextParserTest;
import com.pressir.context.CustomClassLoaderTest;
import com.pressir.context.InvocationContextTest;
import com.pressir.load.DurationParserTest;
import com.pressir.load.PressureTest;
import com.pressir.utils.ReflectUtilsTest;
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
        TFramedTransportFactoryTest.class,
        TSocketFactoryTest.class,
        ContextParserTest.class,
        CustomClassLoaderTest.class,
        InvocationContextTest.class,
        DurationParserTest.class,
        PressureTest.class,
        ReflectUtilsTest.class,
        MainTest.class})
public class Test {}
