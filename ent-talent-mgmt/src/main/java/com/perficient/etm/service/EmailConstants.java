package com.perficient.etm.service;

public final class EmailConstants {

	private EmailConstants(){}
	
	public final class User{
		public static final String FIRST_NAME = "FIRST_NAME";
		public static final String LAST_NAME = "LAST_NAME";
		public static final String EMAIL = "EMAIL";
		public static final String LOGIN = "LOGIN";
		public static final String TITLE = "TITLE";
		public static final String TARGET_TITLE = "TARGET_TITLE";
	}
	
	public final class Review{
		public static final String TYPE = "REVIEW_TYPE";
		public static final String ID = "REVIEW_ID";
	}
	
	
	public final class Templates {
		// Email Templates
		public static final String ACTIVATION = "activationEmail";
		public static final String ANNUAL_REVIEW_STARTED = "reviewStarted";
		public static final String PEER_REVIEW_REMINDER = "peerReviewReminder";
	}
	
	public final class Subjects {
		//Email i18n constants
		public static final String ACTIVATION = "mail.activation.subject";
		public static final String ANNULA_REVIEW_STARTED = "mail.annualreviewstarted.subject";
		public static final String PEER_REVIEW_REMINDER = "mail.peerreviewreminder.subject";
	}
	
}
