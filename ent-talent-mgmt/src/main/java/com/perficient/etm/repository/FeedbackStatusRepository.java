package com.perficient.etm.repository;

import com.perficient.etm.domain.FeedbackStatus;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the FeedbackStatus entity.
 */
public interface FeedbackStatusRepository extends JpaRepository<FeedbackStatus,Long>{

}
