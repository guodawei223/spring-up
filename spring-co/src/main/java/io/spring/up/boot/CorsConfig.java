package io.spring.up.boot;

import io.spring.up.config.Node;
import io.spring.up.log.Log;
import io.spring.up.tool.Ut;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(CorsConfig.class);

    @Bean
    public CorsFilter getCorsFilter() {
        final JsonObject data = Node.infix("cors");
        final JsonObject config = data.getJsonObject("cors");
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        if (null != config) {
            final CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.addAllowedOrigin(Ut.readJson("*", config, "origin"));
            corsConfiguration.addAllowedHeader(Ut.readJson("*", config, "header"));
            corsConfiguration.addAllowedMethod(Ut.readJson("*", config, "method"));
            source.registerCorsConfiguration(Ut.readJson("/**", config, "path"), corsConfiguration);
            Log.info(LOGGER, "[ UP ] Cors configuration has been set : {0}", config.encode());
        }
        return new CorsFilter(source);
    }
}
