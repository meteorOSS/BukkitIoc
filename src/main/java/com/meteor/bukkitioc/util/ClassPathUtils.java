package com.meteor.bukkitioc.util;

import com.meteor.bukkitioc.io.InputStreamCallback;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ClassPathUtils {

    public static <T> T readInputStream(String path, InputStreamCallback<T> inputStreamCallback) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        try (InputStream input = getContextClassLoader().getResourceAsStream(path)) {
            if (input == null) {
                throw new FileNotFoundException("File not found in classpath: " + path);
            }
            return inputStreamCallback.doWithInputStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            throw new UncheckedIOException(e);
        }
    }

    public static <T> T readInputStream(File file,InputStreamCallback<T> inputStreamCallback){
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            return inputStreamCallback.doWithInputStream(fileInputStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readString(String path) {
        return readInputStream(path, (input) -> {
            byte[] bytes = new byte[input.available()];
            while (input.read(bytes)!=-1);
            return new String(bytes, StandardCharsets.UTF_8);
        });
    }

    static ClassLoader getContextClassLoader() {
        ClassLoader cl = null;
        cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = ClassPathUtils.class.getClassLoader();
        }
        return cl;
    }
}