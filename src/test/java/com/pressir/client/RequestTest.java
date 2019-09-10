package com.pressir.client;


import com.pressir.idl.Soda;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

public class RequestTest {

    private File file;

    @Before
    public void getJarFile() {
        file = new File(System.getProperty("user.dir") + "/src/test/resources/base-1.0-SNAPSHOT.jar");
    }

    @Test
    public void parseRequest() throws IOException, ClassNotFoundException, NoSuchMethodException {
        Request request = Request.parseRequest("Soda", "getShopStatus", Collections.singletonList("2019"), file);
        Method method = Soda.Client.class.getDeclaredMethod("getShopStatus", String.class);
        Assert.assertEquals(method, request.getMethod());
        Assert.assertEquals(method.getReturnType(), request.getResult());
        Object[] args = request.parseArguments();
        Assert.assertEquals(1, args.length);
        Assert.assertEquals("2019", args[0].toString());
        Class factory = Soda.Client.Factory.class;
        Assert.assertEquals(factory, request.getInnerFactory().getClass());
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_given_no_argument_specified() throws IOException, ClassNotFoundException {
        Request request = Request.parseRequest("Soda", "getShopStatus", new ArrayList<>(), file);
        request.parseArguments();
    }

    @Test(expected = NoClassDefFoundError.class)
    public void should_throw_exception_given_invalid_service_name() throws IOException, ClassNotFoundException {
        Request.parseRequest("invalid service", "getShopStatus", Collections.singletonList("2019"), file);
    }

    @Test(expected = NoSuchMethodError.class)
    public void should_throw_exception_given_invalid_method_name() throws IOException, ClassNotFoundException {
        Request.parseRequest("Soda", "invalid method", Collections.singletonList("2019"), file);
    }
}
