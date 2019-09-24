package com.pressir;

import com.pressir.printer.ConsolePrinter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

public class ThriftBenchmarkProperties {

    private static Properties properties;
    private static List<String> usageSimple;
    private static List<String> examples;

    static {
        try {
            properties = readAsProperties("thrift-benchmark.properties");
        } catch (Exception e) {
            ConsolePrinter.onError("Cannot find thrift-benchmark.properties");
        }
        try {
            usageSimple = readAsStringList("usage-simple.txt");
        } catch (Exception e) {
            ConsolePrinter.onError("Cannot find usage-simple.txt");
        }
        try {
            examples = readAsStringList("examples.txt");
        } catch (Exception e) {
            ConsolePrinter.onError("Cannot find examples.txt");
        }
    }

    private static List<String> readAsStringList(String fileName) throws URISyntaxException {
        List<String> list = new ArrayList<>();
        Path path = Paths.get(ClassLoader.getSystemResource(fileName).toURI());
        try (Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8)) {
            stream.forEach(s -> list.add(s));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static Properties readAsProperties(String name) {
        Properties properties = new Properties();
        try (InputStream inputStream = ThriftBenchmarkProperties.class.getClassLoader().getResourceAsStream(name)) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            ConsolePrinter.onError("Cannot find thrift-benchmark.properties");
        }
        return properties;
    }

    public static String getProjectName() {
        return properties.getProperty("project.name");
    }

    public static String getProjectVersion() {
        return properties.getProperty("project.version");
    }

    public static List<String> getUsageSimple() {
        return usageSimple;
    }

    public static List<String> getExamples() {
        return examples;
    }
}
