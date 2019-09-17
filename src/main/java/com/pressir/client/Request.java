package com.pressir.client;

import com.pressir.constant.Constants;
import com.pressir.utils.ClassCastUtils;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * @ClassName Request
 * @Description 设计模式+面向对象
 * @Author didi
 * @Date 2019-09-02 16:26
 */
public class Request<T extends TServiceClient> {

    private final Class<T> clientClass;
    private final Method method;
    private Object[] args;

    private Request(Class<T> clientClass, String method, Object[] args) {
        this.method = getMethod(clientClass, method);
        this.clientClass = clientClass;
        this.args = args;
    }

    public static <T extends TServiceClient> Request<T> parseRequest(String service, String method, List<String> args, File jarFile) throws IOException, ClassNotFoundException {
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{jarFile.toURI().toURL()}, Thread.currentThread().getContextClassLoader());
        String clientClass = service + "$Client.class";
        return parseRequest(clientClass, method, args, jarFile, classLoader);
    }

    private static <T extends TServiceClient> Request<T> parseRequest(String clientClassName, String method, List<String> args, File file, ClassLoader classLoader) throws IOException, ClassNotFoundException {
        clientClassName = getServiceName(clientClassName, file).replaceAll("/", ".");
        Class<?> clientClass = classLoader.loadClass(clientClassName);
        return new Request(clientClass, method, args.toArray(new Object[0]));
    }

    private static String getServiceName(String clientClass, File file) throws IOException {
        JarFile jarFile = new JarFile(file);
        Enumeration<JarEntry> entryEnumeration = jarFile.entries();
        boolean existClient = false;
        while (entryEnumeration.hasMoreElements()) {
            JarEntry jarEntry = entryEnumeration.nextElement();
            String className = jarEntry.getName();
            if (className.endsWith(clientClass)) {
                clientClass = className.substring(0, className.lastIndexOf(".class"));
                existClient = true;
                break;
            }
        }
        if (!existClient) {
            throw new NoClassDefFoundError("Can't find " + clientClass + "!");
        }
        return clientClass;
    }

    public Method getMethod() {
        return this.method;
    }

    public Object[] parseArguments() {
        Type[] parameterTypes = this.method.getGenericParameterTypes();
        if (parameterTypes.length == 0) {
            if (this.args != null && this.args.length > 0) {
                throw new IllegalArgumentException("Invalid arguments count in request config. expect: 0, actual: " + this.args.length);
            }
            return new Object[0];
        }
        if (this.args == null) {
            throw new IllegalArgumentException("Null arguments in request config. expect: " + parameterTypes.length);
        }
        if (this.args.length != parameterTypes.length) {
            throw new IllegalArgumentException("Invalid arguments count in request config. expect: " + parameterTypes.length + ", actual: " + this.args.length);
        }
        Object[] arguments = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            arguments[i] = ClassCastUtils.cast(this.args[i], parameterTypes[i]);
        }
        return arguments;
    }


    public TServiceClientFactory<T> getInnerFactory() {
        Class<TServiceClientFactory<T>> clientFactoryClass = null;
        Class<?>[] classes = this.clientClass.getClasses();
        for (Class innerClass : classes) {
            if (!Constants.FACTORY.equals(innerClass.getSimpleName())) {
                continue;
            }
            if (!TServiceClientFactory.class.isAssignableFrom(innerClass)) {
                throw new ClassFormatError("Invalid innerClass: " + innerClass.getName());
            }
            clientFactoryClass = innerClass;
        }
        if (clientFactoryClass == null) {
            throw new NoClassDefFoundError("Can't find Factory in " + this.clientClass.getSimpleName());
        }
        try {
            return clientFactoryClass.newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Error instantiation for class '" + clientFactoryClass.getName() + "' : " + e.getMessage(), e);
        }
    }

    private <T extends TServiceClient> Method getMethod(Class<T> clientClass, String method) {
        Method[] methods = clientClass.getMethods();
        for (Method m : methods) {
            if (m.getName().equals(method)) {
                return m;
            }
        }
        throw new NoSuchMethodError("No Such Method(" + method + ")!");
    }


    public Class<?> getResult() {
        return this.method.getReturnType();
    }
}
