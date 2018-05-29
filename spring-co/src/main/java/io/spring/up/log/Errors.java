package io.spring.up.log;

import io.spring.up.config.Node;
import io.spring.up.exception.internal.ErrorMissingException;
import io.spring.up.tool.Ut;
import io.spring.up.tool.fn.Fn;
import io.vertx.core.json.JsonObject;

import java.text.MessageFormat;

public class Errors {

    public static String formatUp(final Class<?> clazz,
                                  final int code,
                                  final Object... args) {
        return normalize(clazz, code, Tpl.E_UP, args);
    }

    public static String formatPlugin(final Class<?> clazz,
                                      final int code,
                                      final Object... args) {
        return normalize(clazz, code, Tpl.E_PLUGIN, args);
    }

    public static String formatWeb(final Class<?> clazz,
                                   final int code,
                                   final Object... args) {
        return normalize(clazz, code, Tpl.E_WEB, args);
    }

    private static String normalize(final Class<?> clazz,
                                    final int code,
                                    final String tpl,
                                    final Object... args) {
        return Fn.getJvm(() -> {
            final String key = ("E" + Math.abs(code)).intern();
            JsonObject errors = Node.infix("error");
            if (null == errors) {
                throw new ErrorMissingException(code);
            }
            errors = Ut.readJson(new JsonObject(), errors, "error");
            if (errors.containsKey(key)) {
                final String pattern = errors.getString(key);
                final String error = MessageFormat.format(pattern, args);
                return MessageFormat.format(tpl, String.valueOf(code), error);
            } else {
                throw new ErrorMissingException(code);
            }
        }, clazz);
    }
}
