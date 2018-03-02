package io.spring.up.exception.web;

import io.spring.up.core.data.JsonObject;
import io.spring.up.exception.WebException;

public class _400ValidationException extends WebException {

    public _400ValidationException(final Class<?> clazz,
                                   final String field,
                                   final Object value,
                                   final String rule,
                                   final JsonObject config) {
        super(clazz, field, value, rule, config);
    }

    @Override
    public int getCode() {
        return -20004;
    }
}
