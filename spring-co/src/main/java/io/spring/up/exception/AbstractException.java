package io.spring.up.exception;

public abstract class AbstractException extends RuntimeException {
    public AbstractException(final String message) {
        super(message);
    }

    public AbstractException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AbstractException(final Throwable cause) {
        super(cause);
    }

    public abstract int getCode();
}
