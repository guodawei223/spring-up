package com.kingxunlian.micro.up;

import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Future;

@RestController
@RequestMapping("/api")
public class HelloApi {

    @GetMapping("/hello")
    public Future<String> sayHello(final String name) {
        System.out.println(Thread.currentThread().getId());
        return AsyncResult.forValue(name);
    }
}
