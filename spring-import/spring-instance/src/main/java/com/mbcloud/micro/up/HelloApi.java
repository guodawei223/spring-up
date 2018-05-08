package com.mbcloud.micro.up;

import io.spring.up.annotations.JsonBody;
import io.spring.up.core.data.JsonObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class HelloApi {

    @PostMapping("/hello")
    public Mono<String> sayHello(@JsonBody("api.hello.post") final JsonObject data) {
        // System.out.println(Thread.currentThread().getId());
        return Mono.just("Hello");
    }

    @PostMapping("/hello1")
    public String sayHello1(@JsonBody(value = "major", folder = "api.hello.post1") final JsonObject data) {
        return data.encode();
    }
}
