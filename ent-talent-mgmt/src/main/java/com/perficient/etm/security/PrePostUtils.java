package com.perficient.etm.security;

public class PrePostUtils {

	public static final String POST_FILTER_REVIEWS = "filterObject.reviewee.login == principal.username"
												+ " or filterObject.reviewer.login == principal.username"
												+ " or filterObject.reviewee.counselor.login == principal.username"
												+ " or filterObject.reviewee.generalManager.login == principal.username";
	
	public static final String POST_AUTHORIZE_REVIEW = "returnObject != null ? returnObject.reviewee.login == principal.username"
												+ " or returnObject.reviewer.login == principal.username"
												+ " or returnObject.reviewee.counselor.login == principal.username"
												+ " or returnObject.reviewee.generalManager.login == principal.username : true";
}
