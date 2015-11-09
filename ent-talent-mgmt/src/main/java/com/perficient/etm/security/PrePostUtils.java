package com.perficient.etm.security;

public class PrePostUtils {

	public static final String POST_FILTER_REVIEWS = "filterObject.reviewee.login == principal.username"
												+ " or filterObject.reviewer.login == principal.username"
												+ " or filterObject.reviewee.counselor.login == principal.username"
												+ " or filterObject.reviewee.generalManager.login == principal.username"
												+ " or (filterObject.peers.![login].contains(principal.username) and filterObject.reviewStatus.id < 4) ";
	
	public static final String POST_AUTHORIZE_REVIEW = "returnObject == null"
													+ " or returnObject.reviewee.login == principal.username"
													+ " or returnObject.reviewer.login == principal.username"
													+ " or returnObject.reviewee.counselor.login == principal.username"
													+ " or returnObject.reviewee.generalManager.login == principal.username"
													+ " or (returnObject.peers.![login].contains(principal.username) and returnObject.reviewStatus.id < 4) ";
}
