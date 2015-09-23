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
     * Will return a list of all engagements for the user id with the following criteria:
     * engagement startDate is on or after the passed arStartDate (ar = annual review)
     * And engagement endDate is on or before the passed arEndDate
     */
    @Query("SELECT r FROM Review r LEFT JOIN r.reviewType rt ON rt.name = 'Engagement' WHERE r.id = :userId AND r.startDate >= :arStartDate AND r.endDate <= :arEndDate ")
    List<Review> findAllEngagementsWithinAnnualReviewOfUser(@Param("userId") Long userId, @Param("arStartDate")  LocalDate arStartDate, @Param("arEndDate")  LocalDate arEndDate );
} 