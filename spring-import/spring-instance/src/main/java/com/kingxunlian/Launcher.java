package com.kingxunlian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@ComponentScan(basePackages = {"io.spring", "com.kingxunlian"})
public class Launcher {

    public static void main(final String[] args) {
        SpringApplication.run(Launcher.class);
    }
}
