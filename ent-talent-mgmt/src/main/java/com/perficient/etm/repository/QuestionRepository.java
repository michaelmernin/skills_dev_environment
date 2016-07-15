package com.perficient.etm.repository;

import com.perficient.etm.domain.Question;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Spring Data JPA repository for the Question entity.
 */
public interface QuestionRepository extends JpaRepository<Question,Long> {
    @Query(value = "SELECT * FROM T_QUESTION WHERE reviewtype_id = :reviewTypeId AND feedbacktype_id = :feedbackTypeId", nativeQuery = true)
    List<Question> findAllByReviewTypeIdAndFeedbackTypeId(@Param("reviewTypeId") Long reviewTypeId
                                                      , @Param("feedbackTypeId") Long feedbackTypeId);
    
}
