package com.perficient.etm.web.filter.gzip;

import javax.servlet.ServletException;

public class GzipResponseHeadersNotModifiableException extends ServletException {

    private static final long serialVersionUID = -1715534486472532117L;

    public GzipResponseHeadersNotModifiableException(String message) {
        super(message);
    }
}
