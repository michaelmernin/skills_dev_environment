package com.perficient.etm.repository;

import com.perficient.etm.domain.Question;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Question entity.
 */
public interface QuestionRepository extends JpaRepository<Question,Long> {
    
    List<Question> findAllByReviewTypeId(long reviewTypeId);
    
}
