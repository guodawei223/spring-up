package io.spring.up.jhipster.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;

import java.util.HashMap;
import java.util.Map;

import static org.zalando.problem.Status.BAD_REQUEST;

/**
 * Custom, parameterized exception, which can be translated on the client side.
 * For example:
 *
 * <pre>
 * throw new CustomParameterizedException(&quot;myCustomError&quot;, &quot;hello&quot;, &quot;world&quot;);
 * </pre>
 * <p>
 * Can be translated with:
 *
 * <pre>
 * "error.myCustomError" :  "The server says {{param0}} to {{param1}}"
 * </pre>
 */
public class CustomParameterizedException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    private static final String PARAM = "param";

    public CustomParameterizedException(final String message, final String... params) {
        this(message, toParamMap(params));
    }

    public CustomParameterizedException(final String message, final Map<String, Object> paramMap) {
        super(ErrorConstants.PARAMETERIZED_TYPE, "Parameterized Exception", BAD_REQUEST, null, null, null, toProblemParameters(message, paramMap));
    }

    public static Map<String, Object> toParamMap(final String... params) {
        final Map<String, Object> paramMap = new HashMap<>();
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                paramMap.put(PARAM + i, params[i]);
            }
        }
        return paramMap;
    }

    public static Map<String, Object> toProblemParameters(final String message, final Map<String, Object> paramMap) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("message", message);
        parameters.put("params", paramMap);
        return parameters;
    }
}
