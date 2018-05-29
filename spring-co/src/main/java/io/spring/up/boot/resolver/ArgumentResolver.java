package io.spring.up.boot.resolver;

import io.spring.up.annotations.JsonBody;
import io.spring.up.core.rules.Ruler;
import io.spring.up.cv.Encodings;
import io.spring.up.cv.Strings;
import io.spring.up.exception.internal.JsonDecodeException;
import io.spring.up.exception.web._400ParameterMissingException;
import io.spring.up.exception.web._500ParameterTypeException;
import io.spring.up.exception.web._500WebRequestIoException;
import io.spring.up.tool.Ut;
import io.spring.up.tool.fn.Fn;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.io.IOUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.annotation.Annotation;

public class ArgumentResolver implements HandlerMethodArgumentResolver {
    private static final String JSON_REQUEST_BODY = "JSON_REQUEST_BODY";

    @Override
    public boolean supportsParameter(final MethodParameter methodParameter) {
        boolean isMatch = methodParameter.hasParameterAnnotation(JsonBody.class);
        if (isMatch) {
            // 不是JsonObject或JsonArray，类型定义错
            isMatch = Ut.isJObject(methodParameter.getParameterType()) ||
                    Ut.isJArray(methodParameter.getParameterType());
            if (!isMatch) {
                throw new _500ParameterTypeException(this.getClass(), methodParameter.getParameterType());
            }
        }
        return isMatch;
    }

    @Override
    public Object resolveArgument(final MethodParameter methodParameter,
                                  final ModelAndViewContainer modelAndViewContainer,
                                  final NativeWebRequest nativeWebRequest,
                                  final WebDataBinderFactory webDataBinderFactory) throws Exception {
        final String body = this.getRequestBody(methodParameter, nativeWebRequest);
        // 解析参数到固定格式，可支持为空相关计算
        final Object reference = this.resolveArgument(body);
        // 规则基础验证处理
        this.verifyInput(methodParameter, reference);

        return reference;
    }

    private void verifyInput(final MethodParameter methodParameter,
                             final Object reference) {
        final Annotation rule = methodParameter.getParameterAnnotation(JsonBody.class);
        final boolean required = Ut.invoke(rule, "required");
        if (null == reference && required) {
            // 必填选项
            throw new _400ParameterMissingException(this.getClass(),
                    methodParameter.getParameterName(),
                    methodParameter.getMethod().getName());
        } else {
            if (null != reference) {
                final String folder = Ut.invoke(rule, "folder");
                final String value = Ut.invoke(rule, "value");
                // 路径处理
                final String path = this.resolvePath(folder, value);
                if (Ut.isJArray(reference)) {
                    // 数组验证
                    Ruler.verify(path, (JsonArray) reference);
                } else if (Ut.isJObject(reference)) {
                    // 对象验证
                    Ruler.verify(path, (JsonObject) reference);
                }
            }
        }
    }

    private String resolvePath(final String folder, final String value) {
        final String path = folder + Strings.SLASH + value;
        return path.replace("//", "/");
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

    private Object resolveArgument(final String body) {
        return Fn.getJvm(null, () -> {
            try {
                if (body.trim().startsWith(Strings.LEFT_BRACE)) {
                    return new JsonObject(body);
                }
                if (body.trim().startsWith(Strings.LEFT_SQ_BRACKET)) {
                    return new JsonArray(body);
                }
                return null;
            } catch (final JsonDecodeException ex) {
                if (body.trim().startsWith(Strings.LEFT_BRACE)) {
                    throw new _500ParameterTypeException(this.getClass(), JsonObject.class);
                }
                if (body.trim().startsWith(Strings.LEFT_SQ_BRACKET)) {
                    throw new _500ParameterTypeException(this.getClass(), JsonArray.class);
                }
                throw ex;
            }
        }, body);
    }
}
