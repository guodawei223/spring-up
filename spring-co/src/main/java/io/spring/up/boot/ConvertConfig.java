package io.spring.up.boot;

import io.spring.up.boot.converter.JacksonConverter;
import io.spring.up.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
public class ConvertConfig extends WebMvcConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertConfig.class);

    @Override
    public void configureMessageConverters(final List<HttpMessageConverter<?>> converterList) {
        // Jackson2 serialization
        final JacksonConverter converter = new JacksonConverter();
        Log.info(LOGGER, "[ UP ] Converter has been set : {0}", JacksonConverter.class.getName());
        converterList.add(converter);
    }
}
