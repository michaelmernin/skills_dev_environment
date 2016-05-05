package com.perficient.etm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.perficient.etm.domain.SkillRankingHistory;

/**
 * Spring Data JPA repository for the SkillRankingHistory entity.
 */
public interface SkillRankingHistoryRepository extends JpaRepository<SkillRankingHistory,Long>{
    
}

