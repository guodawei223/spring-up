package io.spring.up.jhipster.rest.errors;

import java.io.Serializable;

public class FieldErrorVM implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String objectName;

    private final String field;

    private final String message;

    public FieldErrorVM(final String dto, final String field, final String message) {
        this.objectName = dto;
        this.field = field;
        this.message = message;
    }

    public String getObjectName() {
        return this.objectName;
    }

    public String getField() {
        return this.field;
    }

    public String getMessage() {
        return this.message;
    }

}
