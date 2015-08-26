package com.perficient.etm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.perficient.etm.domain.Review;
import com.perficient.etm.repository.ReviewRepository;
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
 * REST controller for managing Review.
 */
@RestController
@RequestMapping("/api")
public class ReviewResource {

    private final Logger log = LoggerFactory.getLogger(ReviewResource.class);

    @Inject
    private ReviewRepository reviewRepository;

    /**
     * POST  /reviews -> Create a new review.
     */
    @RequestMapping(value = "/reviews",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Review> create(@RequestBody Review review) {
        log.debug("REST request to save Review : {}", review);
        review = reviewRepository.save(review);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    /**
     * GET  /reviews -> get all the reviews.
     */
    @RequestMapping(value = "/reviews",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Review> getAll() {
        log.debug("REST request to get all Reviews");
        return reviewRepository.findAll();
    }

    /**
     * GET  /reviews/:id -> get the "id" review.
     */
    @RequestMapping(value = "/reviews/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Review> get(@PathVariable Long id) {
        log.debug("REST request to get Review : {}", id);
        return Optional.ofNullable(reviewRepository.findOne(id))
            .map(review -> new ResponseEntity<>(
                review,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /reviews/:id -> delete the "id" review.
     */
    @RequestMapping(value = "/reviews/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Review : {}", id);
        reviewRepository.delete(id);
    }
}
