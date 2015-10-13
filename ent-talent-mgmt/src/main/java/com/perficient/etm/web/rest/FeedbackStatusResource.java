package com.perficient.etm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.perficient.etm.domain.FeedbackStatus;
import com.perficient.etm.repository.FeedbackStatusRepository;
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
 * REST controller for managing FeedbackStatus.
 */
@RestController
@RequestMapping("/api")
public class FeedbackStatusResource {

    private final Logger log = LoggerFactory.getLogger(FeedbackStatusResource.class);

    @Inject
    private FeedbackStatusRepository feedbackStatusRepository;

    /**
     * POST  /feedbackStatuses -> Create a new feedbackStatus.
     */
    @RequestMapping(value = "/feedbackStatuses",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody FeedbackStatus feedbackStatus) {
        log.debug("REST request to save FeedbackStatus : {}", feedbackStatus);
        feedbackStatusRepository.save(feedbackStatus);
    }

    /**
     * GET  /feedbackStatuses -> get all the feedbackStatuses.
     */
    @RequestMapping(value = "/feedbackStatuses",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<FeedbackStatus> getAll() {
        log.debug("REST request to get all FeedbackStatuses");
        return feedbackStatusRepository.findAll();
    }

    /**
     * GET  /feedbackStatuses/:id -> get the "id" feedbackStatus.
     */
    @RequestMapping(value = "/feedbackStatuses/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<FeedbackStatus> get(@PathVariable Long id) {
        log.debug("REST request to get FeedbackStatus : {}", id);
        return Optional.ofNullable(feedbackStatusRepository.findOne(id))
            .map(feedbackStatus -> new ResponseEntity<>(
                feedbackStatus,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
