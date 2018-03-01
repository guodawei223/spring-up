package io.spring.up.tool;

import io.spring.up.tool.fn.Fn;

class To {

    static <T extends Enum<T>> T toEnum(final Class<T> clazz, final String input) {
        return Fn.getJvm(null, () -> Enum.valueOf(clazz, input), clazz, input);
    }
}
