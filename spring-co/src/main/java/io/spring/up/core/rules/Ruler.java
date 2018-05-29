package io.spring.up.core.rules;

import io.spring.up.exception.WebException;
import io.spring.up.tool.Ut;
import io.spring.up.tool.fn.Fn;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.text.MessageFormat;

/**
 * 验证规则专用引擎，用于验证Api接口相关数据信息
 */
public class Ruler {
    /**
     * 专用于根路径查找
     */
    private static final String ROOT = "rules/{0}.yml";

    /**
     * 生成最终访问的yml文件
     *
     * @param path
     * @return
     */
    private static JsonObject getConfig(final String path) {
        final String filename = MessageFormat.format(ROOT, path);
        // 池化处理，防止多次加载配置文件
        return Fn.pool(Pool.RULE_MAP, filename, () -> Ut.ioJYaml(filename));
    }

    public static void verify(final String file, final JsonObject data) {
        final JsonObject config = getConfig(file);
        verify(config, data);
    }

    public static void verify(final String file, final JsonArray data) {
        final JsonObject config = getConfig(file);
        Ut.itJArray(data, (item, index) -> verify(config, item));
    }

    private static void verify(final String name, final Object value, final JsonArray rules) {
        Ut.itJArray(rules, (item, index) -> verify(name, value, item));
    }

    private static void verify(final String name, final Object value, final JsonObject rule) {
        final String type = Fn.getNull(() -> rule.getString("type"), rule);
        final JsonObject config = Fn.getNull(() -> rule.getJsonObject("config"), rule);
        Fn.safeNull(() -> {
            final Rule ruler = Rule.get(type);
            Fn.safeNull(() -> {
                final WebException error = ruler.verify(name, value, config);
                Fn.safeNull(() -> {
                    final String message = Fn.getNull(() -> rule.getString("message"), rule);
                    if (null != message) {
                        error.setInfo(message);
                        throw error;
                    }
                }, error);
            }, ruler);
        }, type, config);
    }

    private static void verify(
            final JsonObject config,
            final JsonObject data) {
        Fn.safeNull(() -> Ut.itJObject(data, (field, value) -> {
            // field配置和value的实际验证值
            final Object ruleConfig = config.getValue(field);
            if (null != ruleConfig && Ut.isJArray(ruleConfig)) {
                // 验证当前字段
                verify(field, value, (JsonArray) ruleConfig);
            }
        }), config);
    }
}