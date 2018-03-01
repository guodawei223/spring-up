package io.spring.up.exception.internal;

import java.text.MessageFormat;

public class ErrorMissingException extends InternalException {

    public ErrorMissingException(final int code) {
        super(MessageFormat.format(Message.ECODE_MISSING, String.valueOf(code)), -10004);
    }

    @Override
    public int getCode() {
        return -10004;
    }
}
