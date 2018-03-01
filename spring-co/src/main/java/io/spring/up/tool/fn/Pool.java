package io.spring.up.tool.fn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

class Pool {
    private static final Logger LOGGER = LoggerFactory.getLogger(Pool.class);

    static <K, V> V exec(final ConcurrentMap<K, V> pool,
                         final K key,
                         final Supplier<V> poolFn) {
        if (null == pool || null == key) {
            LOGGER.warn("[ UP ] Pool.exec detect null args: pool = " + pool + ", key = " + key);
            return null;
        }
        V reference = pool.get(key);
        if (null == reference) {
            reference = poolFn.get();
            if (null != reference) {
                pool.put(key, reference);
            }
        }
        return reference;
    }
}
