package com.didiglobal.pressir.thrift.context;

import com.didiglobal.pressir.thrift.base.ServiceClientInvocation;
import com.didiglobal.pressir.thrift.base.transport.TTransportFactory;
import com.didiglobal.pressir.thrift.generator.Generator;
import com.didiglobal.pressir.thrift.generator.InvariantTaskGenerator;
import com.didiglobal.pressir.thrift.utils.ReflectUtils;
import com.google.common.io.Files;
import com.google.common.net.HostAndPort;
import org.apache.logging.log4j.util.Strings;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.protocol.TProtocolFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static com.didiglobal.pressir.thrift.constant.Constants.THRIFT;

/**
 * @ClassName InvocationContext
 * @Description TODO
 * @Author pressir
 * @Date 2019-09-19 21:09
 */
public class InvocationContext {

    private static final String MARK_PATH = "?";

    private static final char MARK_FILE = '@';

    private HostAndPort endpoint;

    private String service;

    private String method;

    private List<String> arguments;

    private CustomClassLoader classLoader;

    private TProtocolFactory protocolFactory;

    private TTransportFactory transportFactory;

    public InvocationContext(File thriftEnv, String uri) {
        this.parseURI(uri);
        this.readThriftEnv(thriftEnv);
    }

    private static Object[] castArgs(Method method, String[] args) {
        try {
            return ReflectUtils.castArgs(method, args);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private static TServiceClientFactory genServiceClientClass(Class<? extends TServiceClient> serviceClientClass) {
        try {
            return (TServiceClientFactory) ReflectUtils.findInnerClass(serviceClientClass, "Factory").newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("serviceClient(" + serviceClientClass.getName() + ") is invalid", e);
        }
    }

    private void parseURI(String uri) {
        uri = uri.substring(THRIFT.length());
        int index = uri.indexOf("/");
        this.endpoint = HostAndPort.fromString(uri.substring(0, index));
        uri = uri.substring(index + 1);
        index = uri.indexOf("/");
        this.service = uri.substring(0, index);
        getMethodAndArgs(uri.substring(index + 1));
    }

    private void getMethodAndArgs(String shard) {
        if (shard.contains(MARK_PATH)) {
            int index = shard.indexOf(MARK_PATH);
            this.method = shard.substring(0, index);
            if (shard.length() > index + 1) {
                char c = shard.charAt(index + 1);
                if (c == MARK_FILE) {
                    readArgsData(new File(shard.substring(index + 2)));
                } else {
                    convertArgs(shard.substring(index + 1));
                }
            } else {
                readArgsData(null);
            }
        } else {
            this.method = shard;
            readArgsData(null);
        }
    }

    private void readThriftEnv(File thriftEnv) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(thriftEnv)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new IllegalStateException("load thrift environment file error cause by " + e.getMessage(), e);
        }
        String clientJar = properties.getProperty("client_jar");
        File jarFile = (clientJar.charAt(0) == File.separatorChar) ?
                new File(clientJar) :
                new File(thriftEnv.getParent(), clientJar);
        try {
            this.classLoader = new CustomClassLoader(jarFile);
        } catch (IOException e) {
            throw new IllegalStateException("client_jar(" + jarFile + ") in thrift environment file is invalid", e);
        }

        String protocol = properties.getProperty("protocol");
        try {
            this.protocolFactory = ContextParser.parseProtocolFactory(protocol);
        } catch (Exception e) {
            throw new IllegalStateException("protocol(" + protocol + ") in thrift environment file is invalid", e);
        }
        if (this.protocolFactory == null) {
            throw new IllegalStateException("protocol(" + protocol + ") in thrift environment file is invalid" + protocol);
        }

        String transport = properties.getProperty("transport");
        try {
            this.transportFactory = ContextParser.parseTransportFactory(transport);
        } catch (Exception e) {
            throw new IllegalStateException("transport(" + transport + ") in thrift environment file is invalid", e);
        }
        if (this.transportFactory == null) {
            throw new IllegalStateException("transport(" + transport + ") in thrift environment file is invalid");
        }
    }

    private void convertArgs(String argsString) {
        if (Strings.isBlank(argsString)) {
            this.arguments = null;
            return;
        }
        this.arguments = Arrays.asList(argsString.split("&"));
    }

    private void readArgsData(File argsData) {
        if (argsData == null) {
            this.arguments = null;
            return;
        }
        if (!argsData.exists()) {
            throw new IllegalStateException("data file(" + argsData.getPath() + ") is missing");
        }
        if (!argsData.isFile()) {
            throw new IllegalStateException(argsData.getPath() + " isn't a file");
        }
        try {
            this.arguments = Files.readLines(argsData, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("args(" + argsData.getPath() + ") is invalid", e);
        }
    }

    public HostAndPort getEndpoint() {
        return endpoint;
    }

    public String getService() {
        return service;
    }

    public String getMethod() {
        return method;
    }

    public <T extends TServiceClient> Generator getTaskGenerator() {
        Class<? extends TServiceClient> serviceClientClass;
        Method method;
        try {
            serviceClientClass =
                    (Class<? extends TServiceClient>) this.classLoader.loadClass(this.service.concat("$Client"));
            method = ReflectUtils.findMethod(serviceClientClass, this.method);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("specified service(" + this.service + ") is invalid", e);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("specified method(" + this.method + ") is invalid", e);
        }
        TServiceClientFactory<T> serviceClientFactory = genServiceClientClass(serviceClientClass);
        ServiceClientInvocation<T> invocation = new ServiceClientInvocation<>(
                serviceClientFactory,
                this.protocolFactory,
                this.transportFactory,
                this.endpoint);
        Object[] args = castArgs(method, this.arguments == null ? null : this.arguments.toArray(new String[0]));
        return new InvariantTaskGenerator<>(invocation, method, args);
    }

}
