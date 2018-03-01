package io.spring.up.tool.fn;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;

class Null {

    static <T> T get(final T defaultValue,
                     final Supplier<T> fnGet,
                     final Object... reference) {
        final boolean match =
                Arrays.stream(reference).allMatch(Objects::nonNull);
        if (match) {
            final T ret = fnGet.get();
            return (null == ret) ? defaultValue : ret;
        } else {
            return defaultValue;
        }
    }

    static <T> void execute(final Actuator actuator,
                            final Object... reference) {
        final boolean match =
                Arrays.stream(reference).allMatch(Objects::nonNull);
        if (match) {
            actuator.execute();
        }
    }
}
