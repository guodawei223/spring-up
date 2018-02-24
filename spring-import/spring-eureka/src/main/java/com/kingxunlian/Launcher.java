package com.kingxunlian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class Launcher {
    public static void main(final String[] args) {
        SpringApplication.run(Launcher.class, args);
    }
}
