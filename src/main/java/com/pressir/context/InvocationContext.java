package com.pressir.context;

import com.google.common.io.Files;
import com.google.common.net.HostAndPort;
import com.pressir.base.ServiceClientInvocation;
import com.pressir.base.transport.TTransportFactory;
import com.pressir.constant.Constants;
import com.pressir.generator.Generator;
import com.pressir.generator.InvariantTaskGenerator;
import com.pressir.utils.ReflectUtils;
import org.apache.logging.log4j.util.Strings;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.protocol.TProtocolFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

/**
 * @ClassName InvocationContext
 * @Description TODO
 * @Author didi
 * @Date 2019-09-19 21:09
 */
public class InvocationContext {

    private HostAndPort endpoint;

    private String service;

    private String method;

    private List<String> arguments;

    private CustomClassLoader classLoader;

    private TProtocolFactory protocolFactory;

    private TTransportFactory transportFactory;

    public InvocationContext(File thriftContext, File argsData, String uri) {
        this.parseURI(uri);
        this.readThriftContext(thriftContext);
        this.readArgsData(argsData);
    }

    private void parseURI(String uri) {
        String[] shards = uri.split("/");
        if (shards.length != Constants.URI_PARTS) {
            throw new IllegalArgumentException("The format of Url is wrong! It should be Host:Port/Service/Method!");
        }
        try {
            this.endpoint = HostAndPort.fromString(shards[0]);
            this.service = shards[1];
            this.method = shards[2];
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid uri: " + uri);
        }
    }

    private void readThriftContext(File thriftContext) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(thriftContext)) {
            properties.load(fis);
        } catch (IOException e) {
            throw new IllegalStateException("Load thrift conf error: " + e.getMessage(), e);
        }
        String classpath = properties.getProperty("classpath");
        if (Strings.isBlank(classpath)) {
            throw new IllegalStateException("Blank 'classpath' in thrift conf");
        }

        File jarFile = (classpath.charAt(0) == File.separatorChar) ? new File(classpath) : new File(thriftContext.getParent(), classpath);
        try {
            this.classLoader = new CustomClassLoader(jarFile);
        } catch (IOException e) {
            throw new IllegalStateException("Error 'classpath' in thrift conf: " + classpath, e);
        }

        String protocol = properties.getProperty("protocol");
        if (Strings.isBlank(protocol)) {
            throw new IllegalStateException("Blank 'protocol' in thrift conf");
        }
        try {
            this.protocolFactory = ContextParser.parseProtocolFactory(protocol);
        } catch (Exception e) {
            throw new IllegalStateException("Error 'protocol' in thrift conf: " + protocol, e);
        }
        if (this.protocolFactory == null) {
            throw new IllegalStateException("Error 'protocol' in thrift conf: " + protocol);
        }

        String transport = properties.getProperty("transport");
        if (Strings.isBlank(protocol)) {
            throw new IllegalStateException("Blank 'transport' in thrift conf");
        }
        try {
            this.transportFactory = ContextParser.parseTransportFactory(transport);
        } catch (Exception e) {
            throw new IllegalStateException("Error 'transport' in thrift conf: " + transport, e);
        }
        if (this.transportFactory == null) {
            throw new IllegalStateException("Error 'transport' in thrift conf: " + transport);
        }
    }

    private void readArgsData(File argsData) {
        if (argsData == null || !argsData.exists()) {
            this.arguments = null;
            return;
        }
        if (!argsData.isFile()) {
            throw new IllegalStateException("Invalid args data: " + argsData.getPath());
        }
        try {
            this.arguments = Files.readLines(argsData, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Invalid args data: " + argsData.getPath(), e);
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
            serviceClientClass = (Class<? extends TServiceClient>) this.classLoader.loadClass(this.service.concat("$Client"));
            method = ReflectUtils.findMethod(serviceClientClass, this.method);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Invalid service: " + this.service, e);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Invalid method: " + this.method, e);
        }
        TServiceClientFactory<T> serviceClientFactory = genServiceClientClass(serviceClientClass);
        ServiceClientInvocation<T> invocation = new ServiceClientInvocation<>(serviceClientFactory, this.protocolFactory, this.transportFactory, this.endpoint);
        Object[] args = castArgs(method, this.arguments == null ? null : this.arguments.toArray(new String[0]));
        return new InvariantTaskGenerator<>(invocation, method, args);
    }

    private static Object[] castArgs(Method method, String[] args) {
        try {
            return ReflectUtils.castArgs(method, args);
        } catch (Exception e) {
            throw new IllegalStateException("Error args: " + e.getMessage(), e);
        }
    }

    private static TServiceClientFactory genServiceClientClass(Class<? extends TServiceClient> serviceClientClass) {
        try {
            return (TServiceClientFactory) ReflectUtils.findInnerClass(serviceClientClass, "Factory").newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Invalid serviceClient: " + serviceClientClass.getName(), e);
        }
    }

}
