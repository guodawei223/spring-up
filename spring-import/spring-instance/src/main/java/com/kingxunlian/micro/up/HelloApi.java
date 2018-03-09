package com.kingxunlian.micro.up;

import io.spring.up.annotations.JsonBody;
import io.spring.up.core.data.JsonObject;
import io.spring.up.plugin.SmsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloApi {

    @Autowired
    private SmsConfig config;

    @PostMapping("/hello")
    public String sayHello(@JsonBody("api.hello.post") final JsonObject data) {
        // System.out.println(Thread.currentThread().getId());
        System.out.println(config.toJson());
        return data.encode();
    }

    @PostMapping("/hello1")
    public String sayHello1(@JsonBody(value = "major", folder = "api.hello.post1") final JsonObject data) {
        return data.encode();
    }
}
