package com.perficient.etm.security;

public class PostFilterUrtils {

	public static final String RESTRICT_REVIEWS = "filterObject.reviewee.login == principal.username"
												+ " or filterObject.reviewer.login == principal.username"
												+ " or filterObject.reviewee.counselor.login == principal.username"
												+ " or filterObject.reviewee.generalManager.login == principal.username";
}
