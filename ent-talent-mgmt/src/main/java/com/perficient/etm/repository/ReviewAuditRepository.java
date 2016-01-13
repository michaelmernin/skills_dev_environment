package com.perficient.etm.repository;

import com.perficient.etm.domain.ReviewAudit;

import org.springframework.data.jpa.repository.*;

/**
 * Spring Data JPA repository for the ReviewAudit entity.
 */
public interface ReviewAuditRepository extends JpaRepository<ReviewAudit,Long> {

}
