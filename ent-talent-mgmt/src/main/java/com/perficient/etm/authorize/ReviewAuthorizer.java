package com.perficient.etm.authorize;

import java.util.Collection;
import java.util.Optional;
import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.User;

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
                        || isSystem(login);
            }).orElse(false);
        }).orElse(true);
    }

    public static boolean filter(Review review) {
        return authorize(review);
    }

    public static boolean isCounselor(Review review, String username) {
        return loginIs(getReviewee(review).map(User::getCounselor), username);
    }

    public static boolean isGeneralManager(Review review, String username) {
        return loginIs(getReviewee(review).map(User::getGeneralManager), username);
    }
    
    public static boolean isDirector(Review review, String username) {
        return loginIs(getReviewee(review).map(User::getDirector), username);
    }

    public static boolean isPeer(Review review, String username) {
        return Optional.ofNullable(review)
            .map(Review::getPeers)
            .map(Collection::stream)
            .map(stream -> stream.map(User::getLogin))
            .map(stream -> stream.anyMatch(loginIs(username)))
            .orElse(false);
    }

    public static boolean isReviewee(Review review, String username) {
        return loginIs(getReviewee(review), username);
    }

    public static boolean isReviewer(Review review, String username) {
        return loginIs(Optional.ofNullable(review).map(Review::getReviewer), username);
    }
    
    private static Optional<User> getReviewee(Review review) {
        return Optional.ofNullable(review).map(Review::getReviewee);
    }
}
