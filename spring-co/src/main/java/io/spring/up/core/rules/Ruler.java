package io.spring.up.core.rules;

import io.spring.up.core.data.JsonArray;
import io.spring.up.core.data.JsonObject;
import io.spring.up.tool.Ut;

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
    private static String produce(final String path) {
        return MessageFormat.format(ROOT, path);
    }

    public static void verify(
            final String file,
            final JsonObject data
    ) {
        final JsonObject config = Ut.ioJYaml(produce(file));
        System.out.println(config);
    }

    public static void verify(
            final String file,
            final JsonArray data
    ) {
        
    }
}