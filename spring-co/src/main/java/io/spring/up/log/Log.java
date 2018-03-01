package io.spring.up.log;

import io.spring.up.tool.fn.Evaluater;
import io.spring.up.tool.fn.Fn;
import org.slf4j.Logger;

import java.text.MessageFormat;
import java.util.function.Consumer;

public class Log {

    public static void jvm(final Logger logger, final Throwable ex) {
        error(logger, Tpl.E_JVM, Fn.getNull("None", () -> ex.getMessage(), ex));
    }

    public static void debug(final Logger logger, final String message, final String... args) {
        output(logger::isDebugEnabled, logger::debug, message, args);
    }

    public static void error(final Logger logger, final String message, final String... args) {
        output(logger::isErrorEnabled, logger::error, message, args);
    }

    public static void warn(final Logger logger, final String message, final String... args) {
        output(logger::isWarnEnabled, logger::info, message, args);
    }

    public static void info(final Logger logger, final String message, final String... args) {
        output(logger::isInfoEnabled, logger::info, message, args);
    }

    private static void output(final Evaluater evaluater, final Consumer<String> fnLog,
                               final String message, final Object... params) {
        if (evaluater.test()) {
            fnLog.accept(format(message, params));
        }
    }

    private static String format(final String pattern, final Object... args) {
        String message = pattern;
        if (0 < args.length) {
            message = MessageFormat.format(message, args);
        }
        return message;
    }
}
