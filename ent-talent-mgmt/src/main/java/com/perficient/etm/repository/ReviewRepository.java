package com.perficient.etm.repository;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.perficient.etm.domain.Review;

/**
 * Spring Data JPA repository for the Review entity.
 */
public interface ReviewRepository extends JpaRepository<Review,Long>{

    /**
     * Will return a list of completed project reviews for a reviewee
     * with a review endDate that is between startDate and endDate.
     */
    @Query("select r from Review r where r.reviewType.interval = 'PROJECT' and r.reviewStatus.id = 5 and r.reviewee.id = :userId and r.endDate > :startDate and r.endDate <= :endDate")
    List<Review> findCompletedEngagementsForUserWithinDates(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
