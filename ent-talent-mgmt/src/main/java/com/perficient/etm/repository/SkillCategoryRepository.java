package com.perficient.etm.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.perficient.etm.domain.SkillCategory;

/**
 * Spring Data JPA repository for the SkillCategory entity.
 */
public interface SkillCategoryRepository extends JpaRepository<SkillCategory,Long>{
    
  /*  @Query("select sc from SkillCategory sc join sc.skill")
    List<SkillCategory> findByRanking(@Param("User")Long userId);*/
    
    //@Query("select c from SkillCategory c left outer join c.skills s left outer join s.rankings r where r.user.id = :userId or r.user.id is null")
    //@Query("select c from SkillCategory c join c.skills s join s.rankings r where r.user.id = :userId")
    @Query(value = "SELECT * FROM T_SKILLCATEGORY c JOIN T_SKILL s ON c.id = s.skillcategory_id LEFT OUTER JOIN T_SKILLRANKING r ON s.id = r.skill_id and r.user_id = ?1", nativeQuery = true)
    Set<SkillCategory> getSkillCategoriesByUserId(Long userId);
    
   // List<SkillCategory> findAllBySkillsRankingsUserId(Long userId);

   // List<SkillCategory> findBySkills();
    List<SkillCategory> findByEnabled(Boolean flag);
}
