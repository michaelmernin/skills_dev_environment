package com.perficient.etm.exception;

import java.util.Arrays;

/**
 * Exception to mark that some of the information needed
 * to start a review process is missing in the system
 * @author Alexandro Blanco <alex.blanco@perficient.com>
 *
 */
public class MissingReviewInfoException extends ETMException{

	private String[] fields;
	
	private static final long serialVersionUID = 5911639227056945155L;

	public MissingReviewInfoException(String... fields) {
		super("Missing information to start review process: ");
		this.fields = fields;
	}

	@Override
	public String getMessage() {
		return super.getMessage() + "["+ Arrays.toString(fields) + "]" ;
	}

	@Override
	public String getLocalizedMessage() {
		return super.getLocalizedMessage() + "["+ Arrays.toString(fields) + "]" ;
	}
	
	
}
