package com.kingxunlian.micro.up;

import io.spring.up.annotations.JsonParam;
import io.spring.up.core.data.JsonObject;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Future;

@RestController
@RequestMapping("/api")
public class HelloApi {

    @PostMapping("/hello")
    public Future<String> sayHello(@JsonParam("/test") final JsonObject data) {
        System.out.println(Thread.currentThread().getId());
        return AsyncResult.forValue(data.encode());
    }
}
