package com.perficient.etm.repository;

import com.perficient.etm.domain.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the ReviewStatus entity.
 */
public interface ReviewStatusRepository extends JpaRepository<ReviewStatus,Long>{

}
