package com.perficient.etm.repository;

import com.perficient.etm.domain.Feedback;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Feedback entity.
 */
public interface FeedbackRepository extends JpaRepository<Feedback,Long> {

    @Query("select feedback from Feedback feedback where feedback.author.login = ?#{principal.username}")
    List<Feedback> findAllForCurrentUser();

    List<Feedback> findAllByReviewId(Long reviewId);

}
