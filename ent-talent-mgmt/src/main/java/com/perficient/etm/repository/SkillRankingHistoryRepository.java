package com.perficient.etm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.perficient.etm.domain.SkillRankingHistory;

/**
 * Spring Data JPA repository for the SkillRankingHistory entity.
 */
public interface SkillRankingHistoryRepository extends JpaRepository<SkillRankingHistory,Long>{
    
    public List<SkillRankingHistory> findBySkillId(Long id);
}

