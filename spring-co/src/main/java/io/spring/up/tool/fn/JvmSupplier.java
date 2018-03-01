package io.spring.up.tool.fn;

@FunctionalInterface
public interface JvmSupplier<T> {

    T get() throws Exception;
}
