package com.didiglobal.pressir.thrift;

import com.didiglobal.pressir.thrift.console.ConsolePrinter;
import org.apache.logging.log4j.util.Strings;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class DeployInfo {
    private static String name = "BenchmarkThrift";
    private static String version = "0.0.1";
    private static String shell = "bt";
    private static Map<String, List<String>> symbolValues = new HashMap<>();

    static {
        try {
            Path path = Paths.get(ClassLoader.getSystemResource(".deploy").toURI());

            try (Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8)) {
                AtomicReference<String> currentSymbol = new AtomicReference<>();
                stream.forEach(s -> {
                    if (s.startsWith(name)) {
                        currentSymbol.set(name);
                    } else if (s.startsWith("Usage")) {
                        currentSymbol.set("Usage");
                    } else if (s.startsWith("Options")) {
                        currentSymbol.set("Options");
                    } else if (s.startsWith("Examples")) {
                        currentSymbol.set("Examples");
                    }
                    if (name.equals(currentSymbol.get())) {
                        String[] split = s.split(":");
                        version = Strings.trimToNull(split[1]);
                    } else {
                        if (!symbolValues.containsKey(currentSymbol.get())) {
                            symbolValues.put(currentSymbol.get(), new ArrayList<>());
                        }
                        symbolValues.get(currentSymbol.get()).add(s);
                    }
                });
            }
        } catch (Exception e) {
            ConsolePrinter.onError("{}: tool is broken, please download and deploy again", shell);
        }
    }

    public static String getName() {
        return name;
    }

    public static String getVersion() {
        return version;
    }

    public static String getShell() {
        return shell;
    }

    public static List<String> getUsage() {
        return symbolValues.get("Usage");
    }

    public static List<String> getExamples() {
        return symbolValues.get("Examples");
    }
}
