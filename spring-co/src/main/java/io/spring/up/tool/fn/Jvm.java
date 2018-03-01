package io.spring.up.tool.fn;

import io.spring.up.exception.AbstractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

class Jvm {

    private static final Logger LOGGER = LoggerFactory.getLogger(Jvm.class);

    static <T> T get(
            final T defaultValue,
            final JvmSupplier<T> supplier,
            final Object... reference
    ) {
        T ret = defaultValue;
        try {
            final boolean match =
                    Arrays.stream(reference).allMatch(Objects::nonNull);
            if (match) {
                ret = supplier.get();
                return (null == ret) ? defaultValue : ret;
            }
        } catch (final AbstractException ex) {
            throw ex;
        } catch (final Throwable ex) {
            LOGGER.error(ex.getMessage());
            // TODO: Debug
            ex.printStackTrace();
        }
        return ret;
    }

    static <T> void execute(
            final JvmActuator actuator,
            final Object... reference
    ) {
        try {
            final boolean match =
                    Arrays.stream(reference).allMatch(Objects::nonNull);
            if (match) {
                actuator.execute();
            }
        } catch (final AbstractException ex) {
            throw ex;
        } catch (final Throwable ex) {
            LOGGER.error(ex.getMessage());
            // TODO: Debug
            ex.printStackTrace();
        }
    }
}
