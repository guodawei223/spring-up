package io.spring.up.boot;

import io.spring.up.tool.Ut;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
public class ConvertConfig extends WebMvcConfigurerAdapter {

    @Override
    public void configureMessageConverters(final List<HttpMessageConverter<?>> converterList) {
        // Jackson2 serialization
        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(Ut.getJacksonMapper());
        converterList.add(converter);
    }
}
