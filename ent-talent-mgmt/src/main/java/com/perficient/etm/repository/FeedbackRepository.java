package com.perficient.etm.repository;

import com.perficient.etm.authorize.FeedbackAuthorizer;
import com.perficient.etm.domain.Feedback;
import com.perficient.etm.domain.FeedbackType;

import org.springframework.data.jpa.repository.*;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;

import java.util.List;
import java.util.Optional;

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
    Optional<Feedback> findOneByReviewIdAndAuthorId(Long reviewId, Long authorId);

    @PostAuthorize(FeedbackAuthorizer.AUTHORIZE)
    Feedback findOne(Long id);

    List<Feedback> findAllByReviewIdAndFeedbackType(Long reviewId, FeedbackType feedbackType);
}
