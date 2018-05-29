package io.spring.up.config;

import io.spring.up.tool.fn.Fn;
import io.vertx.core.json.JsonObject;

public interface Node<T> {

    T read();

    static JsonObject infix(final String filename) {
        final Node<JsonObject> node =
                Fn.pool(Pool.REFERENCES, filename, () -> new ConfigNode(filename));
        return node.read();
    }
}
