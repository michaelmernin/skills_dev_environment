package com.perficient.etm.repository;

import java.util.List;

import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;

import com.perficient.etm.authorize.ReviewAuthorizer;
import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.ReviewStatus;
import com.perficient.etm.domain.ReviewType;

/**
 * Spring Data JPA repository for the Review entity.
 */
public interface ReviewRepository extends JpaRepository<Review,Long> {

    /**
     * Will return a list of completed project reviews for a reviewee
     * with a review endDate that is between startDate and endDate.
     */
    @PostFilter(ReviewAuthorizer.FILTER)
    @Query("select r from Review r where r.reviewType.interval = 'PROJECT' and r.reviewStatus = 5 and r.reviewee.id = :userId and r.endDate > :startDate and r.endDate <= :endDate")
    List<Review> findCompletedEngagementsForUserWithinDates(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @PostFilter(ReviewAuthorizer.FILTER)
    @Override
    List<Review> findAll();

    @PostAuthorize(ReviewAuthorizer.AUTHORIZE)
    @Override
    Review findOne(Long id);

    List<Review> findAllByReviewStatus(ReviewStatus reviewStatus);
    
    List<Review> findAllByReviewTypeAndRevieweeId(ReviewType reviewType, long id);
}
