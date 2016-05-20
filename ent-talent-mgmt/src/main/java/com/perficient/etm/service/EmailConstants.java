package com.perficient.etm.service;

public final class EmailConstants {

	private EmailConstants(){}
	public static final String BASE_URL = "BASE_URL";
	public static final String PROFILE = "PROFILE";
	public static final String PEER_FULL_NAME = "PEER_FULL_NAME";
	public static final String INITIATOR_ID = "INITIATOR_ID";
	
	public final class User{
		public static final String FIRST_NAME = "FIRST_NAME";
		public static final String LAST_NAME = "LAST_NAME";
		public static final String FULL_NAME = "FULL_NAME";
		public static final String ID = "ID";
		public static final String EMAIL = "EMAIL";
		public static final String LOGIN = "LOGIN";
		public static final String TITLE = "TITLE";
		public static final String TARGET_TITLE = "TARGET_TITLE";
	}
	
	public final class Review{
		public static final String TYPE = "REVIEW_TYPE";
		public static final String ID = "REVIEW_ID";
		public static final String STATUS = "REVIEW_STATUS";
		
		public static final String REVIEWEE_FIRST_NAME = "REVIEWEE_FIRST_NAME";
		public static final String REVIEWEE_LAST_NAME = "REVIEWEE_LAST_NAME";
		public static final String REVIEWEE_FULL_NAME = "REVIEWEE_FULL_NAME";
		public static final String REVIEWEE_ID = "REVIEWEE_ID";
		
		public static final String REVIEWER_FIRST_NAME = "REVIEWER_FIRST_NAME";
		public static final String REVIEWER_LAST_NAME = "REVIEWER_LAST_NAME";
		public static final String REVIEWER_FULL_NAME = "REVIEWER_FULL_NAME";
		public static final String REVIEWER_ID = "REVIEWER_ID";
		
	}
	
	
	public final class Templates {
		// Email Templates
		public static final String ACTIVATION = "activationEmail";
		public static final String REVIEW_STARTED = "reviewStarted";
		public static final String PEER_FEEDBACK_REMINDER = "peerFeedbackReminder";
		public static final String PEER_FEEDBACK_REQUESTED = "peerFeedbackRequested";
		public static final String PEER_FEEDBACK_SUBMITTED = "peerFeedbackSubmitted";
	}
	
	public final class Subjects {
		//Email i18n constants
		public static final String ACTIVATION = "mail.activation.subject";
		public static final String ANNULA_REVIEW_STARTED = "mail.annualReview.started.subject";
		public static final String PEER_FEEDBACK_REMINDER = "mail.peerFeedback.reminder.subject";
		public static final String PEER_FEEDBACK_REQUESTED = "mail.peerFeedback.requested.subject";
		public static final String PEER_FEEDBACK_SUBMITTED = "mail.peerFeedback.submitted.subject";
	}
	
}
