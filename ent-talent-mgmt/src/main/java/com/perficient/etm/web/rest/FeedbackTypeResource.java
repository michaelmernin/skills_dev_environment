package com.perficient.etm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.perficient.etm.domain.FeedbackType;
import com.perficient.etm.repository.FeedbackTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing FeedbackType.
 */
@RestController
@RequestMapping("/api")
public class FeedbackTypeResource {

    private final Logger log = LoggerFactory.getLogger(FeedbackTypeResource.class);

    @Inject
    private FeedbackTypeRepository feedbackTypeRepository;

    /**
     * POST  /feedbackTypes -> Create a new feedbackType.
     */
    @RequestMapping(value = "/feedbackTypes",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody FeedbackType feedbackType) {
        log.debug("REST request to save FeedbackType : {}", feedbackType);
        feedbackTypeRepository.save(feedbackType);
    }

    /**
     * GET  /feedbackTypes -> get all the feedbackTypes.
     */
    @RequestMapping(value = "/feedbackTypes",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<FeedbackType> getAll() {
        log.debug("REST request to get all FeedbackTypes");
        return feedbackTypeRepository.findAll();
    }

    /**
     * GET  /feedbackTypes/:id -> get the "id" feedbackType.
     */
    @RequestMapping(value = "/feedbackTypes/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<FeedbackType> get(@PathVariable Long id) {
        log.debug("REST request to get FeedbackType : {}", id);
        return Optional.ofNullable(feedbackTypeRepository.findOne(id))
            .map(feedbackType -> new ResponseEntity<>(
                feedbackType,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /feedbackTypes/:id -> delete the "id" feedbackType.
     */
    @RequestMapping(value = "/feedbackTypes/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete FeedbackType : {}", id);
        feedbackTypeRepository.delete(id);
    }
}
