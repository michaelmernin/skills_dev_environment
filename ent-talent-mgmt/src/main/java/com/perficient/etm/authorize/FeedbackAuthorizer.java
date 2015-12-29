package com.perficient.etm.authorize;

import java.util.Optional;

import com.perficient.etm.domain.Feedback;
import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.ReviewStatus;
import com.perficient.etm.domain.User;


public class FeedbackAuthorizer extends Authorizer{

	private static final String TYPE = TYPE_START + "FeedbackAuthorizer" + TYPE_END;

	public static final String AUTHORIZE = TYPE + AUTHORIZE_METHOD;

	public static final String FILTER = TYPE + FILTER_METHOD;
		
	public static boolean authorize(Feedback feedback) {
		return Optional.ofNullable(feedback).map(nullableFeedback ->{
			return getLogin().map(login -> {
				return isAuthorized(nullableFeedback,login)
					|| isAuthor(nullableFeedback,login);
			}).orElse(false);
		}).orElse(false);
	}

	public static boolean filter(Feedback feedback) {
		return authorize(feedback);
	}
	
	
	/**
	 * checks if isGeneralManager(feedback.review.reviewwee, username)
	 * or isCouncelor(feedback.review.reviewwee, username) 
	 * @param feedback
	 * @param username
	 * @return
	 */
	private static boolean isAuthorized(Feedback feedback, String username) {
		// User reviewer = feedback.getReview().getReviewer();
		return Optional.ofNullable(feedback).map(nullableFeedback -> {
			return Optional.ofNullable(feedback.getReview()).map(review -> {
				return Optional.ofNullable(review.getReviewee()).map(reviewee -> {
					return isGeneralManager(reviewee, username)
						|| isCounselor(reviewee, username)
						|| isReviewee(review,username);
				}).orElse(false);
			}).orElse(false);
		}).orElse(false);
	}
	
	
	/**
	 * Checks if username.equals(feedback.author.login)
	 * @param feedback
	 * @param username
	 * @return
	 */
	private static boolean isAuthor(Feedback feedback, String username) {
		return Optional.ofNullable(feedback.getAuthor()).map(author -> {
			return Optional.ofNullable(author.getLogin()).map(login -> {
				return login.equals(username);
			}).orElse(false);
		}).orElse(false);
	}
	
	
	/**
	 * Checks if username.equals(user.generalManager.login)
	 * @param user
	 * @param username
	 * @return
	 */
	private static boolean isGeneralManager(User user, String username) {
		return Optional.ofNullable(user.getGeneralManager()).map(gm -> {
			return Optional.ofNullable(gm.getLogin()).map(login -> {
				return login.equals(username);
			}).orElse(false);
		}).orElse(false);
	}
	
	
	/**
	 * Checks if username.equals(user.councelor.username)
	 * @param user
	 * @param username
	 * @return
	 */
	private static boolean isCounselor(User user, String username) {
		return Optional.ofNullable(user.getCounselor()).map(counselor -> {
			return Optional.ofNullable(counselor.getLogin()).map(login -> {
				return login.equals(username);
			}).orElse(false);
		}).orElse(false);
	}
	
	
	/**
	 * Checks if username.equals(review.reviewwee.username)
	 * AND review.reviewStatus is JOINT_APPROVAL or GM_APPROVAL or COMPLETE or CLOSED. All have id's >= 2.
	 * @param review
	 * @param username
	 * @return
	 */
	private static boolean isReviewee(Review review, String username) {
		return Optional.ofNullable(review.getReviewee()).map(reviewee -> {
			return Optional.ofNullable(reviewee.getLogin()).map(login -> {
				return login.equals(username)
					&& review.getReviewStatus().getId() >= ReviewStatus.JOINT_APPROVAL.getId();
			}).orElse(false);
		}).orElse(false);
	}
	
}


