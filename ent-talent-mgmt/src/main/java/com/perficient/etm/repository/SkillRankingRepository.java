package com.perficient.etm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.perficient.etm.domain.SkillRanking;

/**
 * Spring Data JPA repository for the SkillRanking entity.
 */
public interface SkillRankingRepository extends JpaRepository<SkillRanking,Long>{
    
    List<SkillRanking> findByUserId(Long id);
}

