package io.spring.up.jhipster.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.spring.up.jhipster.security.SecurityUtils;
import org.springframework.stereotype.Component;

@Component
public class UserFeignClientInterceptor implements RequestInterceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer";

    @Override
    public void apply(final RequestTemplate template) {
        SecurityUtils.getCurrentUserJWT()
                .ifPresent(s -> template.header(AUTHORIZATION_HEADER, String.format("%s %s", BEARER, s)));
    }
}
