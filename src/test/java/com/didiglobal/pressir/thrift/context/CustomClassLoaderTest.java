package com.didiglobal.pressir.thrift.context;


import org.apache.thrift.TServiceClient;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CustomClassLoaderTest {

    @Test
    public void shouldReturnTServiceClient() throws IOException, ClassNotFoundException {
        File jarFile = new File(this.getClass().getClassLoader().getResource("base-1.0-SNAPSHOT.jar").getFile());
        CustomClassLoader classLoader = new CustomClassLoader(jarFile);
        Class<?> clazz = classLoader.loadClass("Soda$Client");
        Assert.assertTrue(TServiceClient.class.isAssignableFrom(clazz));
    }

    @Test(expected = ClassNotFoundException.class)
    public void shouldThrowExceptionForInvalidClass() throws IOException, ClassNotFoundException {
        File jarFile = new File(this.getClass().getClassLoader().getResource("base-1.0-SNAPSHOT.jar").getFile());
        CustomClassLoader classLoader = new CustomClassLoader(jarFile);
        classLoader.loadClass("ABC");
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionForNullFile() throws IOException {
        new CustomClassLoader(null);
    }

    @Test(expected = FileNotFoundException.class)
    public void shouldThrowExceptionForInvalidFile() throws IOException {
        File jarFile = new File("xxx");
        new CustomClassLoader(jarFile);
    }

}
