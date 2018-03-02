package io.spring.up.tool;

import com.esotericsoftware.reflectasm.ConstructorAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import io.spring.up.tool.fn.Fn;

import java.lang.reflect.Constructor;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SuppressWarnings("all")
class Instance {
    /**
     * 单例专用
     **/
    private static final ConcurrentMap<String, Object> SINGLETON
            = new ConcurrentHashMap<>();
    /**
     * 类加载专用：保证整个JVM环境只加载一次
     */
    private static final ConcurrentMap<String, Class<?>> CLASSES
            = new ConcurrentHashMap<>();

    /**
     * 单件模式
     *
     * @param clazz
     * @param args
     * @param <T>
     * @return
     */
    static <T> T singleton(final Class<?> clazz,
                           final Object... args) {
        final Object reference = Fn.getJvm(() ->
                Fn.pool(SINGLETON, clazz.getName(),
                        () -> instance(clazz, args)), clazz);
        return Fn.getJvm(() -> (T) reference, reference);
    }

    /**
     * 实例化模式
     *
     * @param clazz
     * @param args
     * @param <T>
     * @return
     */
    static <T> T instance(final Class<?> clazz,
                          final Object... args) {
        final Object reference = Fn.getJvm(() -> construct(clazz, args), clazz);
        return Fn.getJvm(() -> (T) reference, reference);
    }

    static Class<?> clazz(final String name) {
        return Fn.pool(CLASSES, name,
                // Fix：不使用Class.forName，会引起线程同步安全问题和死锁
                () -> Fn.getJvm(() -> Thread.currentThread().getContextClassLoader().loadClass(name), name));
    }

    /**
     * 反射调用方法
     *
     * @param instance
     * @param name
     * @param args
     * @param <T>
     * @return
     */
    static <T> T invokeObject(
            final Object instance,
            final String name,
            final Object... args
    ) {
        return Fn.getJvm(null, () -> {
            final MethodAccess access = MethodAccess.get(instance.getClass());
            // 直接调用
            final Object result = access.invoke(instance, name, args);
            return null == result ? null : (T) result;
        }, instance, name);
    }

    /**
     * 私有构造函数
     *
     * @param clazz
     * @param args
     * @param <T>
     * @return
     */
    private static <T> T construct(final Class<?> clazz,
                                   final Object... args) {
        return Fn.getJvm(() -> {
            T ret = null;
            final Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            for (final Constructor<?> constructor : constructors) {
                if (0 == args.length) {
                    ret = (T) construct(clazz);
                }
                if (args.length != constructor.getParameterTypes().length) {
                    continue;
                }
                final Object reference = constructor.newInstance(args);
                ret = null == reference ? null : (T) reference;
            }
            return ret;
        }, clazz, args);
    }

    /**
     * 无参构造
     *
     * @param clazz
     * @param <T>
     * @return
     */
    private static <T> T construct(final Class<T> clazz) {
        return Fn.getJvm(() -> {
            final ConstructorAccess<T> access = ConstructorAccess.get(clazz);
            return access.newInstance();
        }, clazz);
    }
}
