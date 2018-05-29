package io.spring.up.boot.advice;

import io.spring.up.exception.WebException;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
@ResponseBody
public class ExceptionAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler(WebException.class)
    public JsonObject handle(final WebException ex,
                             final HttpServletResponse response) {
        response.setStatus(ex.getStatus().value());
        LOGGER.error("[ UP ] Error occurs: " + ex.getMessage());
        return ex.toJson();
    }
}
