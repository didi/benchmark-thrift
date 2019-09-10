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
    public void parseRequest0() throws IOException, ClassNotFoundException {
        Request request = Request.parseRequest("Soda", "getShopStatus", new ArrayList<>(), file);
        request.parseArguments();
    }

    @Test(expected = NoClassDefFoundError.class)
    public void parseRequest1() throws IOException, ClassNotFoundException {
        Request.parseRequest("Soda1", "getShopStatus", Collections.singletonList("2019"), file);
    }

    @Test(expected = NoSuchMethodError.class)
    public void parseRequest2() throws IOException, ClassNotFoundException {
        Request.parseRequest("Soda", "getShopStatus1", Collections.singletonList("2019"), file);
    }
}
