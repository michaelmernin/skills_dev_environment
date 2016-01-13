package com.perficient.etm.repository;

import com.perficient.etm.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Question entity.
 */
public interface QuestionRepository extends JpaRepository<Question,Long> {

}
