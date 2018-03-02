package io.spring.up.aop;

import io.spring.up.core.data.JsonObject;
import io.spring.up.exception.WebException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
@ResponseBody
public class ExceptionAdvice {

    @ExceptionHandler(WebException.class)
    public JsonObject handle(final WebException ex,
                             final HttpServletResponse response) {
        response.setStatus(ex.getStatus().value());
        return ex.toJson();
    }
}
