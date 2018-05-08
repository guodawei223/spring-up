package io.spring.up.boot.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "up",
        ignoreUnknownFields = false
)
public class UpProperty {
}
