package io.spring.up.boot.converter;

import io.vertx.core.json.JsonObject;

/**
 * 响应专用数据处理
 */
public interface Responser {
    /**
     * 处理响应方法
     *
     * @param original 原始数据格式
     * @param value    目前的响应数据
     * @return 生成新的数据格式
     */
    JsonObject process(final JsonObject original, final Object value);
}
