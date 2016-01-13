package com.perficient.etm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -9085805045353318850L;

    public ResourceNotFoundException(String msg) {
        super(msg);
    }
}
