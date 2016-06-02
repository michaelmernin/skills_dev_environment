package com.perficient.etm.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Set;

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
import com.perficient.etm.domain.Rating;
import com.perficient.etm.exception.ResourceNotFoundException;
import com.perficient.etm.repository.FeedbackRepository;
import com.perficient.etm.repository.RatingRepository;

/**
 * REST controller for managing Rating.
 */
@RestController
@RequestMapping("/api")
public class RatingResource implements RestResource {

    private final Logger log = LoggerFactory.getLogger(RatingResource.class);

    @Inject
    private RatingRepository ratingRepository;
    
    @Inject
    private FeedbackRepository feedbackRepository;

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
     * PUT  /reviews/:reviewId/feedback/:feedbackId/ratings/:ratingId -> Updates an existing rating.
     */
    @RequestMapping(value = "/reviews/{reviewId}/feedback/{feedbackId}/ratings/{ratingId}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody Rating rating, @PathVariable Long reviewId, @PathVariable Long feedbackId, @PathVariable Long ratingId) throws URISyntaxException {
        log.debug("REST request to update Rating : {}", rating);
        // if user does not have access to feedback, user does not have access to rating either
        return Optional.ofNullable(feedbackRepository.findOne(feedbackId)).map(feedback -> {
            return Optional.ofNullable(ratingRepository.findOne(ratingId)).map(oldRating -> {
                oldRating.setComment(rating.getComment());
                oldRating.setQuestion(rating.getQuestion());
                oldRating.setScore(rating.getScore());
                oldRating.setVisible(rating.isVisible());
                ratingRepository.save(oldRating);
                return ResponseEntity.ok().build();
            }).orElse(ResponseEntity.badRequest().build());
        }).orElse(ResponseEntity.badRequest().build());
        
    }

    /**
     * GET  /reviews/:reviewId/feedback/:feedbackId/ratings -> get all the ratings.
     */
    @RequestMapping(value = "/reviews/{reviewId}/feedback/{feedbackId}/ratings",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Set<Rating>> getAll(@PathVariable Long reviewId, @PathVariable Long feedbackId) {
        log.debug("REST request to get all Ratings");
        return Optional.ofNullable(feedbackRepository.findOne(feedbackId))
                .map(feedback -> new ResponseEntity<>(feedback.getRatings(), HttpStatus.OK))
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("feedback " + feedbackId +" cannot be found or you don't have access");
                });
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
            .orElseThrow(() -> {
                return new ResourceNotFoundException("Rating " + id + " cannot be found.");
            });
    }
}
