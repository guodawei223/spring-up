package io.spring.up.core.rules;

import io.spring.up.core.data.JsonObject;
import io.spring.up.exception.WebException;

public class RequiredRule implements Rule {
    @Override
    public WebException verify(final String field,
                               final Object value,
                               final JsonObject config) {
        return Rule.verify(this.getClass(), () -> null == value, field, value, "required", config);
    }
}
