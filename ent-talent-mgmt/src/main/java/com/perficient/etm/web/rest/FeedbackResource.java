package com.perficient.etm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.perficient.etm.domain.Feedback;
import com.perficient.etm.repository.FeedbackRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Feedback.
 */
@RestController
@RequestMapping("/api")
public class FeedbackResource {

    private final Logger log = LoggerFactory.getLogger(FeedbackResource.class);

    @Inject
    private FeedbackRepository feedbackRepository;

    /**
     * POST  /feedback -> Create a new feedback.
     */
    @RequestMapping(value = "/feedback",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody Feedback feedback) throws URISyntaxException {
        log.debug("REST request to save Feedback : {}", feedback);
        if (feedback.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new feedback cannot already have an ID").build();
        }
        feedbackRepository.save(feedback);
        return ResponseEntity.created(new URI("/api/feedback/" + feedback.getId())).build();
    }

    /**
     * PUT  /feedback -> Updates an existing feedback.
     */
    @RequestMapping(value = "/feedback",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody Feedback feedback) throws URISyntaxException {
        log.debug("REST request to update Feedback : {}", feedback);
        if (feedback.getId() == null) {
            return create(feedback);
        }
        feedbackRepository.save(feedback);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /feedback -> get all the feedback.
     */
    @RequestMapping(value = "/feedback",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Feedback> getAll() {
        log.debug("REST request to get all Feedbacks");
        return feedbackRepository.findAll();
    }

    /**
     * GET  /feedback/:id -> get the "id" feedback.
     */
    @RequestMapping(value = "/feedback/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Feedback> get(@PathVariable Long id) {
        log.debug("REST request to get Feedback : {}", id);
        return Optional.ofNullable(feedbackRepository.findOne(id))
            .map(feedback -> new ResponseEntity<>(
                feedback,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /feedback/:id -> delete the "id" feedback.
     */
    @RequestMapping(value = "/feedback/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Feedback : {}", id);
        feedbackRepository.delete(id);
    }
}
