package com.pressir.context;


import org.apache.thrift.TServiceClient;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CustomClassLoaderTest {

    @Test
    public void should_return_TServiceClient() throws IOException, ClassNotFoundException {
        File jarFile = new File(this.getClass().getClassLoader().getResource("base-1.0-SNAPSHOT.jar").getFile());
        CustomClassLoader classLoader = new CustomClassLoader(jarFile);
        Class<?> clazz = classLoader.loadClass("Soda$Client");
        Assert.assertTrue(TServiceClient.class.isAssignableFrom(clazz));
    }

    @Test(expected = ClassNotFoundException.class)
    public void should_throw_exception_4_invalid_class() throws IOException, ClassNotFoundException {
        File jarFile = new File(this.getClass().getClassLoader().getResource("base-1.0-SNAPSHOT.jar").getFile());
        CustomClassLoader classLoader = new CustomClassLoader(jarFile);
        classLoader.loadClass("ABC");
    }

    @Test(expected = NullPointerException.class)
    public void should_throw_exception_4_null_file() throws IOException {
        new CustomClassLoader(null);
    }

    @Test(expected = FileNotFoundException.class)
    public void should_throw_exception_4_invalid_file() throws IOException {
        File jarFile = new File("xxx");
        new CustomClassLoader(jarFile);
    }

}
