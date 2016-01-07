package com.perficient.etm.exception;

import java.util.Arrays;

/**
 * Exception to mark that some of the information needed
 * to start a review process is missing in the system
 * @author Alexandro Blanco <alex.blanco@perficient.com>
 *
 */
public class MissingReviewInfoException extends ETMException {

    private static final long serialVersionUID = 8912140073173975618L;

    public MissingReviewInfoException(String... fields) {
		super("Missing information to start review process: " + Arrays.toString(fields));
	}

}
