package com.kingxunlian.micro;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloApi {

    @RequestMapping("/hello")
    public String sayHello(){
        return "Hello World";
    }
}
