package com.kingxunlian.micro.up;

import io.spring.up.annotations.JsonBody;
import io.spring.up.core.data.JsonObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloApi {

    @PostMapping("/hello1")
    public String sayHello1(@JsonBody(value = "major", folder = "api.hello.post1") final JsonObject data) {
        return data.encode();
    }
}
