package com.perficient.etm.repository;

import com.perficient.etm.authorize.FeedbackAuthorizer;
import com.perficient.etm.domain.Feedback;
import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.User;

import org.springframework.data.jpa.repository.*;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;

import java.util.List;

/**
 * Spring Data JPA repository for the Feedback entity.
 */
public interface FeedbackRepository extends JpaRepository<Feedback,Long> {

    @PostFilter(FeedbackAuthorizer.FILTER)
    @Query("select feedback from Feedback feedback where feedback.author.login = ?#{principal.username}")
    List<Feedback> findAllForCurrentUser();

    @PostFilter(FeedbackAuthorizer.FILTER)
    List<Feedback> findAllByReviewId(Long reviewId);
    
    @PostAuthorize(FeedbackAuthorizer.AUTHORIZE)
    Feedback findOneByReviewAndAuthor(Review review, User author);

    @PostAuthorize(FeedbackAuthorizer.AUTHORIZE)
    Feedback findOne(Long id);
}
