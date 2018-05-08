package io.spring.up.boot.model;

public interface UpProfiles {
    // 开发专用Profile
    String SPRING_DEVELOPMENT = "dev";
    // 测试专用Profile
    String SPRING_TEST = "test";
    // 生产专用Profile
    String SPRING_PRODUCTION = "prod";
    // 云端专用Profile
    String SPRING_CLOUD = "cloud";
    // Heroku专用Profile
    String SPRING_HEROKU = "heroku";
    // Swagger用的Profile
    String SPRING_SWAGGER = "swagger";
    // No-Liquibase专用Profile
    String SPRING_NO_LIQUIBASE = "no-liquibase";
    // K8s专用Profile
    String SPRING_K8S = "k8s";
}
