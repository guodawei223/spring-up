package io.spring.up.config;

import io.spring.up.atom.JsonObject;
import io.spring.up.tool.fn.Fn;

public interface Node<T> {

    T read();

    static JsonObject infix(final String key) {
        final Node<JsonObject> node =
                Fn.pool(Pool.REFERENCES, key, () -> new ConfigNode(key));
        return node.read();
    }
}
