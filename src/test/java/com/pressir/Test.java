package com.pressir;

import com.didiglobal.pressir.thrift.base.transport.TFramedTransportFactoryTest;
import com.didiglobal.pressir.thrift.base.transport.TSocketFactoryTest;
import com.didiglobal.pressir.thrift.context.ContextParserTest;
import com.didiglobal.pressir.thrift.context.CustomClassLoaderTest;
import com.didiglobal.pressir.thrift.context.InvocationContextTest;
import com.didiglobal.pressir.thrift.load.DurationParserTest;
import com.didiglobal.pressir.thrift.load.PressureTest;
import com.didiglobal.pressir.thrift.utils.ReflectUtilsTest;
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
