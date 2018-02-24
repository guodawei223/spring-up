package io.spring.up.exception;

public abstract class SpringRunException extends RuntimeException {
    public SpringRunException(final String message) {
        super(message);
    }

    public SpringRunException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public SpringRunException(final Throwable cause) {
        super(cause);
    }
}
