package com.perficient.etm.web.rest;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.perficient.etm.domain.Question;
import com.perficient.etm.exception.ResourceNotFoundException;
import com.perficient.etm.repository.QuestionRepository;

/**
 * REST controller for managing Question.
 */
@RestController
@RequestMapping("/api")
public class QuestionResource implements RestResource {

    private final Logger log = LoggerFactory.getLogger(QuestionResource.class);

    @Inject
    private QuestionRepository questionRepository;

    /**
     * POST  /questions -> Create a new question.
     */
    @RequestMapping(value = "/questions",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody Question question) {
        log.debug("REST request to save Question : {}", question);
        questionRepository.save(question);
    }

    /**
     * GET  /questions -> get all the questions.
     */
    @RequestMapping(value = "/questions",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Question> getAll() {
        log.debug("REST request to get all Questions");
        return questionRepository.findAll();
    }

    /**
     * GET  /questions/:id -> get the "id" question.
     */
    @RequestMapping(value = "/questions/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Question> get(@PathVariable Long id) {
        log.debug("REST request to get Question : {}", id);
        return Optional.ofNullable(questionRepository.findOne(id))
            .map(question -> new ResponseEntity<>(
                question,
                HttpStatus.OK))
            .orElseThrow(() -> {
                return new ResourceNotFoundException("Question " + id + " cannot be found.");
            });
    }
    
    /**
     * GET  /questions/:reviewTypeId/:feedbackTypeId -> get the "id" question.
     */
    @RequestMapping(value = "/questions/{reviewTypeId}/{feedbackTypeId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Question>> getByFeedbackTypeAndReviewType(@PathVariable Long reviewTypeId,@PathVariable Long feedbackTypeId) {
        log.debug("REST request to get Questions by Feedback Type: {} and Review Type: {}", feedbackTypeId, reviewTypeId);
        return Optional.ofNullable(questionRepository.findAllByReviewTypeId(reviewTypeId))
            .map(questions -> new ResponseEntity<>(
                questions,
                HttpStatus.OK))
            .orElseThrow(() -> {
                return new ResourceNotFoundException("Questions with feedbackTypeId: " + feedbackTypeId + " and reviewTypeId: "+ reviewTypeId +" cannot be found.");
            });
    }
}
