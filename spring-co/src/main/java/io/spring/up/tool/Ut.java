package io.spring.up.tool;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.function.BiConsumer;

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

    public static <T> T invoke(final Object instance, final String methodName, final Object... args) {
        return Instance.invokeObject(instance, methodName, args);
    }

    public static Class<?> clazz(final String name) {
        return Instance.clazz(name);
    }

    // 集合遍历专用方法
    public static void itJObject(final JsonObject object, final BiConsumer<String, Object> consumer) {
        It.itJObject(object, consumer);
    }

    public static <T> void itJArray(final JsonArray array, final Class<T> clazz, final BiConsumer<T, Integer> consumer) {
        It.itJArray(array, clazz, consumer);
    }

    // 快速方法（遍历元素为JsonObject的数组）
    public static void itJArray(final JsonArray array, final BiConsumer<JsonObject, Integer> consumer) {
        It.itJArray(array, JsonObject.class, consumer);
    }

    // 类型判断
    public static boolean isPositive(final String original) {
        return Numeric.isPositive(original);
    }

    public static boolean isNegative(final String original) {
        return Numeric.isNegative(original);
    }

    public static boolean isReal(final String original) {
        return Numeric.isReal(original);
    }

    public static boolean isDecimal(final String original) {
        return Numeric.isDecimal(original);
    }

    public static boolean isDecimal(final Object value) {
        return Types.isDecimal(value);
    }

    public static boolean isDecimal(final Class<?> clazz) {
        return Types.isDecimal(clazz);
    }

    // 类型判断
    public static boolean isInteger(final String original) {
        return Numeric.isInteger(original);
    }

    public static boolean isInteger(final Object value) {
        return Types.isInteger(value);
    }

    public static boolean isInteger(final Class<?> clazz) {
        return Types.isInteger(clazz);
    }

    public static boolean isJArray(final Object value) {
        return Types.isJArray(value);
    }

    public static boolean isJArray(final Class<?> clazz) {
        return Types.isJArray(clazz);
    }

    public static boolean isJObject(final Class<?> clazz) {
        return Types.isJObject(clazz);
    }

    public static boolean isJObject(final Object value) {
        return Types.isJObject(value);
    }

    public static boolean isBoolean(final Object value) {
        return Types.isBoolean(value);
    }

    public static boolean isBoolean(final Class<?> clazz) {
        return Types.isBoolean(clazz);
    }

    public static boolean isPrimary(final Class<?> clazz) {
        return Types.isPrimary(clazz);
    }

    public static boolean isVoid(final Class<?> clazz) {
        return Types.isVoid(clazz);
    }

    public static boolean isDate(final Object value) {
        return Types.isDate(value);
    }

    // 值域处理
    public static boolean inRange(final Integer value, final Integer min, final Integer max) {
        return Numeric.inRange(value, min, max);
    }

    // 读取数据
    public static Object readJson(final JsonObject data, final String key) {
        return Jackson.readJson(null, data, key);
    }

    public static Object readJson(final Object value, final JsonObject data, final String key) {
        return Jackson.readJson(value, data, key);
    }

    public static JsonObject readJson(final JsonObject value, final JsonObject data, final String key) {
        final Object result = Jackson.readJson(value, data, key);
        return null == result ? new JsonObject() : (JsonObject) result;
    }

    public static String readJson(final String value, final JsonObject data, final String key) {
        final Object result = Jackson.readJson(value, data, key);
        return null == result ? value : result.toString();
    }

    public static Integer readInt(final Integer value, final JsonObject data, final String key) {
        return Jackson.readInt(value, data, key);
    }

    // 类型转换
    public static <T extends Enum<T>> T toEnum(final Class<T> clazz, final String input) {
        return To.toEnum(clazz, input);
    }

    public static LocalDateTime toDateTime(final String literal) {
        return Period.toDateTime(literal);
    }

    public static LocalDate toDate(final String literal) {
        return Period.toDate(literal);
    }

    public static LocalTime toTime(final String literal) {
        return Period.toTime(literal);
    }

    public static int toMonth(final String literal) {
        return Period.toMonth(literal);
    }

    public static int toMonth(final Date date) {
        return Period.toMonth(date);
    }

    public static int toYear(final String literal) {
        return Period.toYear(literal);
    }

    public static int toYear(final Date date) {
        return Period.toYear(date);
    }

    public static Integer toInteger(final Object value) {
        return To.toInteger(value);
    }

    /**
     * 加密
     *
     * @param literal
     * @return
     */
    public static String encryptMD5(final String literal) {
        return Codec.encryptMD5(literal);
    }

    public static String encryptSHA256(final String literal) {
        return Codec.encryptSHA256(literal);
    }

    public static String encryptSHA512(final String literal) {
        return Codec.encryptSHA512(literal);
    }

    /**
     * 时间全解析
     *
     * @param literal
     * @return
     */
    public static Date parse(final String literal) {
        return Period.parse(literal);
    }

    public static Date parse(final LocalTime time) {
        return Period.parse(time);
    }

    public static Date parse(final LocalDateTime datetime) {
        return Period.parse(datetime);
    }

    public static Date parse(final LocalDate date) {
        return Period.parse(date);
    }

    public static Date parseFull(final String literal) {
        return Period.parseFull(literal);
    }

    /**
     * Json序列化
     */
    public static <T, R extends Iterable> R serializeJson(final T t) {
        return Jackson.serializeJson(t);
    }

    public static <T> String serialize(final T t) {
        return Jackson.serialize(t);
    }

    public static <T> T deserialize(final JsonObject value, final Class<T> type) {
        return Jackson.deserialize(value, type);
    }

    public static <T> T deserialize(final JsonArray value, final Class<T> type) {
        return Jackson.deserialize(value, type);
    }

    public static <T> List<T> deserialize(final JsonArray value, final TypeReference<List<T>> type) {
        return Jackson.deserialize(value, type);
    }

    public static <T> T deserialize(final String value, final Class<T> type) {
        return Jackson.deserialize(value, type);
    }

    public static <T> T deserialize(final String value, final TypeReference<T> type) {
        return Jackson.deserialize(value, type);
    }

    public static ObjectMapper getJacksonMapper() {
        return Jackson.getMapper();
    }

    /**
     * 网络
     */
    public static boolean netOk(final String host, final int port) {
        return Net.isReach(host, port);
    }

    public static String netIPv4() {
        return Net.getIPv4();
    }

    public static String netIPv6() {
        return Net.getIPv6();
    }

    public static String netHostname() {
        return Net.netHostname();
    }

    public static String netIP() {
        return Net.getIP();
    }
}
