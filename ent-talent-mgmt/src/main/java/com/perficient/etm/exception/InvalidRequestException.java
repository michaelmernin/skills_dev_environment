package com.perficient.etm.exception;

import org.springframework.validation.Errors;

public class InvalidRequestException extends RuntimeException {

    private static final long serialVersionUID = -8470540857938536060L;

    private final Errors errors;

    public InvalidRequestException(String message, Errors errors) {
        super(message);
        this.errors = errors;
    }

    public Errors getErrors() {
        return errors;
    }
}
