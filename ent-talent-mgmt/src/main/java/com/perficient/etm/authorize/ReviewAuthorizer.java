package com.perficient.etm.authorize;

import java.util.Optional;

import com.perficient.etm.domain.Review;

public class ReviewAuthorizer extends Authorizer {
    
    private static final String TYPE = TYPE_START + "ReviewAuthorizer" + TYPE_END;

    public static final String AUTHORIZE = TYPE + AUTHORIZE_METHOD;

    public static final String FILTER = TYPE + FILTER_METHOD;

    public static boolean authorize(Review review) {
        return getLogin().map(login -> {
	        return Optional.ofNullable(review).map(optionalReview -> {
	           	return  isReviewer(optionalReview, login)
	           			|| isReviewee(optionalReview, login)
	           			|| isCounselor(optionalReview, login)
	           			|| isGeneralManager(optionalReview, login)
	           			|| isPeer(optionalReview, login);
	           }).orElse(false);
	        }).orElse(false);
    }

    public static boolean filter(Review review) {
        return authorize(review);
    }

    private static boolean isCounselor(Review review, String username) {
        return review.getReviewee().getCounselor().getLogin().equals(username);
    }

    private static boolean isGeneralManager(Review review, String username) {
        return review.getReviewee().getGeneralManager().getLogin().equals(username);
    }

    private static boolean isPeer(Review review, String username) {
        return review.getPeers().stream().anyMatch(u -> {
            return u.getLogin().equals(username);
        });
    }

    private static boolean isReviewee(Review review, String username) {
        return review.getReviewee().getLogin().equals(username);
    }

    private static boolean isReviewer(Review review, String username) {
        return review.getReviewer().getLogin().equals(username);
    }
}
