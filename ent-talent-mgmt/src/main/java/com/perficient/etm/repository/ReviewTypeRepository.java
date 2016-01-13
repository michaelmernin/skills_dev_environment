package com.perficient.etm.repository;

import com.perficient.etm.domain.ReviewType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the ReviewType entity.
 */
public interface ReviewTypeRepository extends JpaRepository<ReviewType,Long> {

}
