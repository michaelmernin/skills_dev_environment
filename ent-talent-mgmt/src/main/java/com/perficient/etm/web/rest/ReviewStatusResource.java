package com.perficient.etm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.perficient.etm.domain.ReviewStatus;
import com.perficient.etm.repository.ReviewStatusRepository;
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
 * REST controller for managing ReviewStatus.
 */
@RestController
@RequestMapping("/api")
public class ReviewStatusResource {

    private final Logger log = LoggerFactory.getLogger(ReviewStatusResource.class);

    @Inject
    private ReviewStatusRepository reviewStatusRepository;

    /**
     * POST  /reviewStatuses -> Create a new reviewStatus.
     */
    @RequestMapping(value = "/reviewStatuses",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody ReviewStatus reviewStatus) {
        log.debug("REST request to save ReviewStatus : {}", reviewStatus);
        reviewStatusRepository.save(reviewStatus);
    }

    /**
     * GET  /reviewStatuses -> get all the reviewStatuses.
     */
    @RequestMapping(value = "/reviewStatuses",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ReviewStatus> getAll() {
        log.debug("REST request to get all ReviewStatuses");
        return reviewStatusRepository.findAll();
    }

    /**
     * GET  /reviewStatuses/:id -> get the "id" reviewStatus.
     */
    @RequestMapping(value = "/reviewStatuses/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ReviewStatus> get(@PathVariable Long id) {
        log.debug("REST request to get ReviewStatus : {}", id);
        return Optional.ofNullable(reviewStatusRepository.findOne(id))
            .map(reviewStatus -> new ResponseEntity<>(
                reviewStatus,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
