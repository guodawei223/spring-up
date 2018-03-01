package io.spring.up.boot;

import io.spring.up.config.Node;
import io.spring.up.core.data.JsonObject;
import io.spring.up.tool.Ut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 *
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter getCorsFilter() {
        final JsonObject data = Node.infix("cors");
        final JsonObject config = data.getJsonObject("cors");
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin(Ut.readJson("*", config, "origin"));
        corsConfiguration.addAllowedHeader(Ut.readJson("*", config, "header"));
        corsConfiguration.addAllowedMethod(Ut.readJson("*", config, "method"));
        source.registerCorsConfiguration(Ut.readJson("/**", config, "path"), corsConfiguration);
        return new CorsFilter(source);
    }
}
