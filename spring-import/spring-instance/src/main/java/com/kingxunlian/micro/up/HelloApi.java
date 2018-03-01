package com.kingxunlian.micro.up;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloApi {

    @GetMapping("/hello")
    public String sayHello(
            @RequestBody final String name
    ) {
        System.out.println(name);
        return "Hello World";
    }
}
