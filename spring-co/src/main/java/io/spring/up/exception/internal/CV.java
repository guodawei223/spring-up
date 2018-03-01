package io.spring.up.exception.internal;

interface Message {

    String ERROR_INTERNAL = "[ UP{1} ] System detect internal error = {0}.";

    String EMPTY_STREAM = "Empty Stream found when read file {0}.";

    String ECODE_MISSING = "The code = {0} of error is missing in your file: application-error.yml.";
}
