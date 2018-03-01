package io.spring.up.exception;

import io.spring.up.core.data.JsonObject;
import io.spring.up.cv.Strings;
import io.spring.up.log.Errors;
import org.springframework.http.HttpStatus;

import java.util.Objects;

public abstract class WebException extends AbstractException {

    protected static final String INFO = "info";
    
    private final String message;

    protected HttpStatus status;

    private String readible;

    public WebException(final String message) {
        super(message);
        this.message = message;
        this.status = HttpStatus.BAD_REQUEST;
    }

    public WebException(final Class<?> clazz, final Object... args) {
        super(Strings.EMPTY);
        this.message = Errors.formatWeb(clazz, this.getCode(), args);
        this.status = HttpStatus.BAD_REQUEST;
    }

    @Override
    public abstract int getCode();

    @Override
    public String getMessage() {
        return this.message;
    }

    public HttpStatus getStatus() {
        // Default exception for 400
        return this.status;
    }

    public void setReadible(final String readible) {
        this.readible = readible;
    }

    public void setStatus(final HttpStatus status) {
        this.status = status;
    }

    public String getReadible() {
        return this.readible;
    }

    @Override
    public JsonObject toJson() {
        final JsonObject data = new JsonObject();
        data.put(CODE, this.getCode());
        data.put(MESSAGE, this.getMessage());
        if (Objects.nonNull(this.readible)) {
            data.put(INFO, this.readible);
        }
        return data;
    }
}
