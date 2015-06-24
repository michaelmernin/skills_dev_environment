package com.perficient.etm.repository;

import com.perficient.etm.domain.FeedbackType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the FeedbackType entity.
 */
public interface FeedbackTypeRepository extends JpaRepository<FeedbackType,Long>{

}
