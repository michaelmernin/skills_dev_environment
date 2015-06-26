package com.perficient.etm.repository;

import com.perficient.etm.domain.Rating;
import org.springframework.data.jpa.repository.*;

/**
 * Spring Data JPA repository for the Rating entity.
 */
public interface RatingRepository extends JpaRepository<Rating,Long> {

}
