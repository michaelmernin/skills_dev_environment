package com.perficient.etm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.perficient.etm.domain.ReviewType;
import com.perficient.etm.exception.ResourceNotFoundException;
import com.perficient.etm.repository.ReviewTypeRepository;
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
 * REST controller for managing ReviewType.
 */
@RestController
@RequestMapping("/api")
public class ReviewTypeResource implements RestResource {

    private final Logger log = LoggerFactory.getLogger(ReviewTypeResource.class);

    @Inject
    private ReviewTypeRepository reviewTypeRepository;

    /**
     * POST  /reviewTypes -> Create a new reviewType.
     */
    @RequestMapping(value = "/reviewTypes",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody ReviewType reviewType) {
        log.debug("REST request to save ReviewType : {}", reviewType);
        reviewTypeRepository.save(reviewType);
    }

    /**
     * GET  /reviewTypes -> get all the reviewTypes.
     */
    @RequestMapping(value = "/reviewTypes",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ReviewType> getAll() {
        log.debug("REST request to get all ReviewTypes");
        return reviewTypeRepository.findAll();
    }
    
    /**
     * GET  /reviewTypes -> get all the reviewTypes except annual review.
     */
    @RequestMapping(value = "/reviewTypes/allExceptAR",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ReviewType> getAllExceptAR() {
        log.debug("REST request to get all ReviewTypes");
        ReviewType type = new ReviewType();
        type.setId((long)1);
        List<ReviewType> list = reviewTypeRepository.findAll();
        list.remove(type);
        return list;
    }

    /**
     * GET  /reviewTypes/:id -> get the "id" reviewType.
     */
    @RequestMapping(value = "/reviewTypes/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ReviewType> get(@PathVariable Long id) {
        log.debug("REST request to get ReviewType : {}", id);
        return Optional.ofNullable(reviewTypeRepository.findOne(id))
            .map(reviewType -> new ResponseEntity<>(
                reviewType,
                HttpStatus.OK))
            .orElseThrow(() -> {
                return new ResourceNotFoundException("Review Type " + id + " cannot be found.");
            });
    }
}
