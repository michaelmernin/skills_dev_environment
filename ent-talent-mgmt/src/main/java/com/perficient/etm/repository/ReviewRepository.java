package com.perficient.etm.repository;

import com.perficient.etm.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Review entity.
 */
public interface ReviewRepository extends JpaRepository<Review,Long>{

}
