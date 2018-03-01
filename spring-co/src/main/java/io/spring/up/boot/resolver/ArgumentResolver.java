package io.spring.up.boot.resolver;

import io.spring.up.annotations.JsonParam;
import io.spring.up.cv.Encodings;
import io.spring.up.exception.web._500WebRequestIoException;
import org.apache.commons.io.IOUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class ArgumentResolver implements HandlerMethodArgumentResolver {
    private static final String JSON_REQUEST_BODY = "JSON_REQUEST_BODY";

    @Override
    public boolean supportsParameter(final MethodParameter methodParameter) {
        System.out.println("Hello");
        System.out.println(methodParameter.hasParameterAnnotation(JsonParam.class));
        return methodParameter.hasParameterAnnotation(JsonParam.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter methodParameter,
                                  final ModelAndViewContainer modelAndViewContainer,
                                  final NativeWebRequest nativeWebRequest,
                                  final WebDataBinderFactory webDataBinderFactory) throws Exception {
        System.out.println("Error");
        throw new _500WebRequestIoException(methodParameter.getMethod().getDeclaringClass(), null);
        // final String body = this.getRequestBody(methodParameter, nativeWebRequest);

        // return null;
    }

    private String getRequestBody(final MethodParameter methodParameter,
                                  final NativeWebRequest request) {
        final HttpServletRequest servletRequest =
                request.getNativeRequest(HttpServletRequest.class);
        String jsonBody = (String) servletRequest.getAttribute(JSON_REQUEST_BODY);
        if (jsonBody == null) {
            try {
                jsonBody = IOUtils.toString(servletRequest.getInputStream(), Encodings.CHARSET);
                servletRequest.setAttribute(JSON_REQUEST_BODY, jsonBody);
            } catch (final IOException e) {
                throw new _500WebRequestIoException(methodParameter.getMethod().getDeclaringClass(), e);
            }
        }
        return jsonBody;
    }
}
