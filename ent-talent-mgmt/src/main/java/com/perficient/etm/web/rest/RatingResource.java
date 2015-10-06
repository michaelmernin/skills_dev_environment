package com.perficient.etm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.perficient.etm.domain.Rating;
import com.perficient.etm.repository.RatingRepository;
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
 * REST controller for managing Rating.
 */
@RestController
@RequestMapping("/api")
public class RatingResource {

    private final Logger log = LoggerFactory.getLogger(RatingResource.class);

    @Inject
    private RatingRepository ratingRepository;

    /**
     * POST  /reviews/:reviewId/feedback/:feedbackId/ratings -> Create a new rating.
     */
    @RequestMapping(value = "/reviews/{reviewId}/feedback/{feedbackId}/ratings",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody Rating rating) throws URISyntaxException {
        log.debug("REST request to save Rating : {}", rating);
        if (rating.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new rating cannot already have an ID").build();
        }
        ratingRepository.save(rating);
        return ResponseEntity.created(new URI("/api/ratings/" + rating.getId())).build();
    }

    /**
     * PUT  /reviews/:reviewId/feedback/:feedbackId/ratings -> Updates an existing rating.
     */
    @RequestMapping(value = "/reviews/{reviewId}/feedback/{feedbackId}/ratings",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody Rating rating) throws URISyntaxException {
        log.debug("REST request to update Rating : {}", rating);
        if (rating.getId() == null) {
            return create(rating);
        }
        ratingRepository.save(rating);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /reviews/:reviewId/feedback/:feedbackId/ratings -> get all the ratings.
     */
    @RequestMapping(value = "/reviews/{reviewId}/feedback/{feedbackId}/ratings",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Rating> getAll() {
        log.debug("REST request to get all Ratings");
        return ratingRepository.findAll();
    }

    /**
     * GET  /reviews/:reviewId/feedback/:feedbackId/ratings/:id -> get the "id" rating.
     */
    @RequestMapping(value = "/reviews/{reviewId}/feedback/{feedbackId}/ratings/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Rating> get(@PathVariable Long id) {
        log.debug("REST request to get Rating : {}", id);
        return Optional.ofNullable(ratingRepository.findOne(id))
            .map(rating -> new ResponseEntity<>(
                rating,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
