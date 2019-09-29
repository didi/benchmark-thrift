package com.didiglobal.pressir.thrift.context;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @ClassName CustomClassLoader
 * @Description TODO
 * @Author pressir
 * @Date 2019-09-19 22:04
 */
public class CustomClassLoader implements Closeable {
    private final JarFile jarFile;

    private final URLClassLoader classLoader;

    CustomClassLoader(File jarFile) throws IOException {
        if (jarFile == null) {
            throw new NullPointerException("Null file");
        }
        if (!jarFile.exists() || !jarFile.isFile()) {
            throw new FileNotFoundException("Invalid file: " + jarFile.getPath());
        }
        this.jarFile = new JarFile(jarFile);
        this.classLoader = URLClassLoader.newInstance(
                new URL[]{jarFile.toURI().toURL()},
                URLClassLoader.getSystemClassLoader());
    }

    Class<?> loadClass(String className) throws ClassNotFoundException {
        String classFileName = className.replace('.', File.separatorChar).concat(".class");
        Enumeration<JarEntry> enumeration = jarFile.entries();
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = enumeration.nextElement();
            if (jarEntry.isDirectory()) {
                continue;
            }
            String jarEntryName = jarEntry.getName();
            int lengthDiff = jarEntryName.length() - classFileName.length();
            if (lengthDiff < 0) {
                continue;
            } else if (lengthDiff > 0) {
                if (jarEntryName.charAt(lengthDiff - 1) != File.separatorChar) {
                    continue;
                }
                if (!jarEntryName.endsWith(classFileName)) {
                    continue;
                }
            } else {
                if (!jarEntryName.equals(classFileName)) {
                    continue;
                }
            }
            String name = jarEntryName.substring(0, jarEntryName.length() - 6).replace(File.separatorChar, '.');
            return this.classLoader.loadClass(name);
        }
        throw new ClassNotFoundException("Invalid class: " + className);
    }

    @Override
    public void close() throws IOException {
        this.classLoader.close();
    }
}
