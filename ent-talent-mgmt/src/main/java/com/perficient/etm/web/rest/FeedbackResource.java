package com.perficient.etm.web.rest;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import com.perficient.etm.domain.Feedback;
import com.perficient.etm.domain.FeedbackStatus;
import com.perficient.etm.domain.User;
import com.perficient.etm.exception.InvalidRequestException;
import com.perficient.etm.exception.ResourceNotFoundException;
import com.perficient.etm.repository.FeedbackRepository;
import com.perficient.etm.security.SecurityUtils;
import com.perficient.etm.service.FeedbackService;
import com.perficient.etm.web.view.View;

/**
 * REST controller for managing Feedback for a Review.
 */
@RestController
@RequestMapping("/api")
public class FeedbackResource implements RestResource {

    private final Logger log = LoggerFactory.getLogger(FeedbackResource.class);

    @Inject
    private FeedbackRepository feedbackRepository;

    
    @Inject
    private FeedbackService feedbackService;

    /**
     * PUT /reviews/:id/feedback -> Updates an existing feedback.
     * @return 
     * @return 
     */
    @RequestMapping(value = "/reviews/{reviewId}/feedback", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@PathVariable Long reviewId, @RequestBody Feedback feedback,
            BindingResult result) {
        log.debug("REST request to update Feedback : {} for Review : {}", feedback, reviewId);
        if (result.hasErrors()) {
            throw new InvalidRequestException("Invalid feedback update", result);
        }
        log.debug("REST request to save Feedback : {} for Review : {}", feedback, reviewId);
        if (result.hasErrors()) {
            throw new InvalidRequestException("Invalid new feedback", result);
        }
        ResponseEntity<Void> badrequest = ResponseEntity.badRequest().build();
        return SecurityUtils.getPrincipal().map(principal -> {
            return Optional.ofNullable(feedback).map(newFeedback ->{
                return Optional.ofNullable(newFeedback.getId()).map(newFeedbackId ->{
                    return Optional.ofNullable(feedbackRepository.findOne(newFeedbackId)).map(existingFeedback ->{
                        return Optional.ofNullable(existingFeedback.getAuthor()).map(User::getId).map(existingAuthorId ->{
                            if(existingFeedback.isAuthor(principal)){
                                return Optional.ofNullable(existingFeedback.getFeedbackStatus()).map(FeedbackStatus::getId).map(existingDFeedbackStatusId ->{
                                    if(existingDFeedbackStatusId < FeedbackStatus.READY.getId()){
                                        existingFeedback.setFeedbackStatus(newFeedback.getFeedbackStatus());
                                        existingFeedback.setOverallComment(newFeedback.getOverallComment());
                                        existingFeedback.setOverallScore(newFeedback.getOverallScore());
                                        feedbackRepository.save(existingFeedback);
                                        return ResponseEntity.ok().build();
                                    }
                                    return badrequest;
                                }).orElse(badrequest);
                            }
                            return badrequest;
                        }).orElse(badrequest);
                    }).orElse(badrequest);
                }).orElse(badrequest);
            }).orElse(badrequest);
        }).orElse(badrequest);
    }
        
    /**
     * GET /reviews/:id/feedback -> get the feedback for a review.
     */
    @RequestMapping(value = "/reviews/{reviewId}/feedback", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Feedback> getAll(@PathVariable Long reviewId) {
        log.debug("REST request to get all Feedback for Review : {}", reviewId);
        return feedbackRepository.findAllByReviewId(reviewId);
    }

    /**
     * GET /reviews/:id/feedback/:id -> get the "id" feedback.
     */
    @RequestMapping(value = "/reviews/{reviewId}/feedback/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Feedback> get(@PathVariable Long reviewId, @PathVariable Long id) {
        log.debug("REST request to get Feedback : {} for Review : {}", id, reviewId);
        return Optional.ofNullable(feedbackRepository.findOne(id))
                .map(feedback -> new ResponseEntity<>(feedback, HttpStatus.OK))
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Feedback " + id + " for Review " + reviewId + " cannot be found.");
                });
    }
    
    /**
     * PUT  /reviews/:reviewId/feedback/:id/open
     */
    @RequestMapping(value = "/reviews/{reviewId}/feedback/{id}/open",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @JsonView(View.Public.class)
    public ResponseEntity<Feedback> open(@PathVariable Long reviewId, @PathVariable Long id) {
        log.debug("REST request to open peer Feedback : {} for Review : {}", id, reviewId);
        return Optional.ofNullable(feedbackRepository.findOne(id))
                .map(feedbackService::openPeerFeedback)
                .map(feedback -> new ResponseEntity<>(feedback, HttpStatus.OK))
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Feedback " + id + " for Review " + reviewId + " cannot be found.");
                });
    }
}
