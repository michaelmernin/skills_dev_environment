package com.perficient.etm.exception;

/**
 * Base exception for all the checked exceptions in the ETM platform
 * @author Alexandro Blanco <alex.blanco@perficient.com>
 *
 */
public class ETMException extends RuntimeException {

    private static final long serialVersionUID = -7849765648678750790L;

    public ETMException(String msg) {
        super(msg);
    }

    public ETMException(String msg, Throwable t) {
        super(msg, t);
    }
}
