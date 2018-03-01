package io.spring.up.config;

import io.spring.up.atom.JsonObject;
import org.junit.Test;

public class ConfigTestCase {

    @Test
    public void testErrorNode() {
        final JsonObject data = Node.infix("error");
        System.out.println(data);
    }
}
