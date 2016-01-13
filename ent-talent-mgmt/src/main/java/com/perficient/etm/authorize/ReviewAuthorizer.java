package com.perficient.etm.authorize;

import java.util.Optional;

import com.perficient.etm.domain.Review;

public class ReviewAuthorizer extends Authorizer {

    private static final String TYPE = TYPE_START + "ReviewAuthorizer" + TYPE_END;

    public static final String AUTHORIZE = TYPE + AUTHORIZE_METHOD;

    public static final String FILTER = TYPE + FILTER_METHOD;

    public static boolean authorize(Review review) {
        return Optional.ofNullable(review).map(optionalReview -> {
            return getLogin().map(login -> {
                return isReviewer(optionalReview, login)
                        || isReviewee(optionalReview, login)
                        || isCounselor(optionalReview, login)
                        || isGeneralManager(optionalReview, login)
                        || isPeer(optionalReview, login);
            }).orElse(false);
        }).orElse(true);
    }

    public static boolean filter(Review review) {
        return authorize(review);
    }

    private static boolean isCounselor(Review review, String username) {
        return Optional.ofNullable(review).map(nullableReview -> {
            return Optional.ofNullable(nullableReview.getReviewee()).map(reviewee -> {
                return Optional.ofNullable(reviewee.getCounselor()).map(counselor -> {
                    return Optional.ofNullable(counselor.getLogin()).map(login -> {
                        return login.equals(username);
                    }).orElse(false);
                }).orElse(false);
            }).orElse(false);
        }).orElse(false);
    }

    private static boolean isGeneralManager(Review review, String username) {
        return Optional.ofNullable(review).map(nullableReview -> {
            return Optional.ofNullable(nullableReview.getReviewee()).map(reviewee -> {
                return Optional.ofNullable(reviewee.getGeneralManager()).map(gm -> {
                    return Optional.ofNullable(gm.getLogin()).map(login -> {
                        return login.equals(username);
                    }).orElse(false);
                }).orElse(false);
            }).orElse(false);
        }).orElse(false);
    }

    private static boolean isPeer(Review review, String username) {
        return Optional.ofNullable(review).map(nullableReview -> {
            return Optional.ofNullable(nullableReview.getPeers()).map(peers -> {
                return peers.stream().anyMatch(u -> {
                    return u.getLogin().equals(username);
                });
            }).orElse(false);
        }).orElse(false);
    }

    private static boolean isReviewee(Review review, String username) {
        return Optional.ofNullable(review).map(nullableReview -> {
            return Optional.ofNullable(nullableReview.getReviewee()).map(reviewee -> {
                return Optional.ofNullable(reviewee.getLogin()).map(login -> {
                    return login.equals(username);
                }).orElse(false);
            }).orElse(false);
        }).orElse(false);
    }

    private static boolean isReviewer(Review review, String username) {
        return Optional.ofNullable(review).map(nullableReview -> {
            return Optional.ofNullable(nullableReview.getReviewer()).map(reviewer -> {
                return Optional.ofNullable(reviewer.getLogin()).map(login -> {
                    return login.equals(username);
                }).orElse(false);
            }).orElse(false);
        }).orElse(false);

        // review.getReviewer().getLogin().equals(username);
    }
}
