package com.kingxunlian.micro.up;

import io.spring.up.annotations.EndPoint;
import io.spring.up.annotations.Post;
import org.springframework.web.bind.annotation.RequestBody;

@EndPoint
public class HelloApi {

    @Post("/hello")
    public String sayHello(
            @RequestBody final String name
    ) {
        System.out.println(name);
        return "Hello World";
    }
}
