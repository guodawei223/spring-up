package io.spring.up.store;

import io.spring.up.core.data.JsonObject;
import org.junit.Test;

public class ConfigTestCase {

    @Test
    public void testError() {
        final JsonObject data = Node.infix("error");
        System.out.println(data);
    }

    @Test
    public void testCors() {
        final JsonObject data = Node.infix("cors");
        System.out.println(data);
    }
}
