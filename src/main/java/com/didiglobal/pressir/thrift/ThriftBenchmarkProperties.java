package com.didiglobal.pressir.thrift;

import com.pressir.printer.ConsolePrinter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class ThriftBenchmarkProperties {

    private static Properties properties;
    private static Map<String, List<String>> symbolValues;

    static {
        try {
            properties = readAsProperties("thrift-benchmark.properties");
        } catch (Exception e) {
            ConsolePrinter.onError("Cannot find thrift-benchmark.properties");
        }
        try {
            symbolValues = readAsStringList("usage.txt");
        } catch (Exception e) {
            ConsolePrinter.onError("Cannot find usage.txt");
        }
    }

    private static Map<String, List<String>> readAsStringList(String fileName) throws Exception {
        Map<String, List<String>> map = new HashMap<>();
        Path path = Paths.get(ClassLoader.getSystemResource(fileName).toURI());

        try (Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8)) {
            AtomicReference<String> currentSymbol = new AtomicReference<>();
            stream.forEach(s -> {
                if (s.startsWith("Usage")) {
                    currentSymbol.set("Usage");
                } else if (s.startsWith("Options")) {
                    currentSymbol.set("Usage");
                } else if (s.startsWith("Examples")) {
                    currentSymbol.set("Examples");
                }
                if (!map.containsKey(currentSymbol.get())) {
                    map.put(currentSymbol.get(), new ArrayList<>());
                }
                map.get(currentSymbol.get()).add(s);
            });
        }
        return map;
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

    public static List<String> getUsage() {
        return symbolValues.get("Usage");
    }

    public static List<String> getExamples() {
        return symbolValues.get("Examples");
    }
}
