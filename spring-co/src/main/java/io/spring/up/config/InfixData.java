package io.spring.up.config;

import io.spring.up.core.data.JsonObject;

/**
 *
 */
public interface InfixData<T> {

    JsonObject toJson();

    T fromJson(JsonObject config);
}
