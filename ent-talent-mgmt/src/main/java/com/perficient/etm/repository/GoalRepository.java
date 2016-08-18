package com.perficient.etm.repository;

import com.perficient.etm.domain.Goal;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Goal entity.
 */
public interface GoalRepository extends JpaRepository<Goal,Long> {

    @Query("select goal from Goal goal where goal.author.login = ?#{principal.username}")
    List<Goal> findAllForCurrentUser();
    
    List<Goal> findAllByReviewId(Long reviewId);

}
