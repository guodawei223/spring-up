package io.spring.up.tool;

import io.spring.up.tool.fn.Fn;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.function.BiConsumer;

@SuppressWarnings("all")
class It {

    static <T> void itJArray(final JsonArray array,
                             final Class<T> clazz,
                             final BiConsumer<T, Integer> consumer) {
        Fn.safeNull(() -> {
            final int length = array.size();
            for (int idx = 0; idx < length; idx++) {
                final Object value = array.getValue(idx);
                if (null != value && clazz == value.getClass()) {
                    final T param = (T) value;
                    // 参数：值和索引
                    consumer.accept(param, idx);
                }
            }
        }, array, clazz);
    }

    static void itJObject(final JsonObject object,
                          final BiConsumer<String, Object> consumer) {
        object.forEach(item -> {
            if (null != item) {
                final String key = item.getKey();
                final Object value = item.getValue();
                if (null != key && null != value) {
                    // 参数：键和值
                    consumer.accept(key, value);
                }
            }
        });
    }
}
