package io.spring.up.tool.io;

import io.spring.up.atom.JsonArray;
import io.spring.up.atom.JsonObject;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class Ut {
    // IO专用方法
    public static Properties ioProp(final String filename) {
        return IO.getProp(filename);
    }

    public static File ioFile(final String filename) {
        return IO.getFile(filename);
    }

    public static URL ioURL(final String filename) {
        return IO.getURL(filename);
    }

    public static String ioString(final String filename) {
        return IO.getString(filename);
    }

    public static JsonArray ioJArray(final String filename) {
        return IO.getJArray(filename);
    }

    public static JsonObject ioJObject(final String filename) {
        return IO.getJObject(filename);
    }

    public static <T> T ioJYaml(final String filename) {
        return IO.getJYaml(filename);
    }

    public static InputStream ioStream(final String filename) {
        return IO.getStream(filename);
    }

    // 反射方法
    public static <T> T instance(final String name, final Object... args) {
        return Instance.instance(Instance.clazz(name), args);
    }

    public static <T> T instance(final Class<?> clazz, final Object... args) {
        return Instance.instance(clazz, args);
    }

    public static <T> T singleton(final String name, final Object... args) {
        return Instance.singleton(Instance.clazz(name), args);
    }

    public static <T> T singleton(final Class<?> clazz, final Object... args) {
        return Instance.singleton(clazz, args);
    }

    public static Class<?> clazz(final String name) {
        return Instance.clazz(name);
    }
}
