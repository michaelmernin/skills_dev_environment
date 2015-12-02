package com.perficient.etm.exception;

import com.perficient.etm.domain.ReviewType;

/**
 * Exception class that will mark when a review process
 * can't be started in Activiti engine because of the
 * desired review type is not recognized by the system
 * 
 * @author Alexandro Blanco <alex.blanco@perficient.com>
 */
public class ReviewProcessNotFound extends ETMException {

	private static final long serialVersionUID = -1069208892635434961L;

	public ReviewProcessNotFound(String msg){
		super(msg);
	}
	
	public ReviewProcessNotFound(String msg, Throwable t){
		super(msg, t);
	}
	
	public ReviewProcessNotFound(ReviewType type){
	    super("Review type "+((type != null)?type.getName():" null ")+" not found");	
	}
}
