package io.spring.up.exception.internal;

import io.spring.up.exception.AbstractException;

import java.text.MessageFormat;

public abstract class InternalException extends AbstractException {

    public InternalException(final String message, final int code) {
        super(MessageFormat.format(Message.ERROR_INTERNAL, message, String.valueOf(code)));
    }
}
