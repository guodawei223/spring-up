package io.spring.up.store;

import io.spring.up.core.data.JsonObject;
import io.spring.up.tool.fn.Fn;

public interface Node<T> {

    T read();

    static JsonObject infix(final String filename) {
        final Node<JsonObject> node =
                Fn.pool(Pool.REFERENCES, filename, () -> new ConfigNode(filename));
        return node.read();
    }
}
