package com.perficient.etm.web.rest;

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
import com.perficient.etm.domain.Feedback;
import com.perficient.etm.domain.FeedbackStatus;
import com.perficient.etm.domain.Rating;
import com.perficient.etm.exception.ResourceNotFoundException;
import com.perficient.etm.repository.FeedbackRepository;
import com.perficient.etm.repository.RatingRepository;
import com.perficient.etm.security.SecurityUtils;

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
     * PUT  /reviews/:reviewId/feedback/:feedbackId/ratings/:ratingId -> Updates an existing rating.
     */
    @RequestMapping(value = "/reviews/{reviewId}/feedback/{feedbackId}/ratings/{ratingId}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody Rating rating, @PathVariable Long reviewId, @PathVariable Long feedbackId, @PathVariable Long ratingId) throws URISyntaxException {
        log.debug("REST request to update Rating : {}", rating);
        ResponseEntity<Void> badRequest = ResponseEntity.badRequest().build();
        // if user does not have access to feedback, user does not have access to rating either
        return Optional.ofNullable(feedbackRepository.findOne(feedbackId)).map(feedback -> {
            return Optional.ofNullable(ratingRepository.findOne(ratingId)).map(oldRating -> {
                return Optional.ofNullable(feedback.getFeedbackStatus()).map(FeedbackStatus::getId).map(feedbackStatusId ->{
                    if(feedbackStatusId < FeedbackStatus.READY.getId()){
                        oldRating.setComment(rating.getComment());
                        //oldRating.setQuestion(rating.getQuestion());
                        oldRating.setScore(rating.getScore());
                        updateVisibility(oldRating, rating.isVisible());
                        ratingRepository.save(oldRating);
                        return ResponseEntity.ok().build();
                    }
                    return badRequest;
                }).orElse(badRequest);
            }).orElse(badRequest);
        }).orElse(badRequest);
        
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
        // if user has access to feedback, they have access to ratings within that feedback
        return Optional.ofNullable(feedbackRepository.findOne(feedbackId))
                .map(feedback -> new ResponseEntity<>(feedback.getRatings(), HttpStatus.OK))
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("feedback " + feedbackId +" cannot be found or you don't have access");
                });
    }

    /**
     * GET  /reviews/:reviewId/feedback/:feedbackId/ratings/:id -> get the "id" rating.
     */
    @RequestMapping(value = "/reviews/{reviewId}/feedback/{feedbackId}/ratings/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Rating> get(@PathVariable Long id) {
        log.debug("REST request to get Rating : {}", id);
        ResponseEntity<Rating> unauthorized = new ResponseEntity<>(new Rating(), HttpStatus.UNAUTHORIZED);
        return SecurityUtils.getPrincipal().map(principal -> {
            return Optional.ofNullable(ratingRepository.findOne(id)).map(rating -> {
                return Optional.ofNullable(rating.getFeedback()).map(feeedback -> {
                    if (feeedback.isAuthor(principal)) {
                        return new ResponseEntity<>(rating, HttpStatus.OK);
                    }
                    return unauthorized;
                }).orElse(unauthorized);
            }).orElse(unauthorized);
        }).orElse(unauthorized);
    }

    private void updateVisibility(Rating rating, boolean visibility) {
        SecurityUtils.getPrincipal().ifPresent(principal -> {
            // update visibility ONLY if the requesting user is the reviewer
            Optional.ofNullable(rating).map(Rating::getFeedback).map(Feedback::getReview).ifPresent(review -> {
                if (review.isReviewer(principal)) {
                    rating.setVisible(visibility);
                }
            });
        });
        return;
    }

}
