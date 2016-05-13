package com.perficient.etm.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.perficient.etm.domain.Skill;

/**
 * Spring Data JPA repository for the Skill entity.
 */
public interface SkillRepository extends JpaRepository<Skill,Long>{

}
