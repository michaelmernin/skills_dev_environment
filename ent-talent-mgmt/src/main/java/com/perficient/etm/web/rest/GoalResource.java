package com.perficient.etm.web.rest;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

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
import com.perficient.etm.domain.Goal;
import com.perficient.etm.domain.Review;
import com.perficient.etm.exception.InvalidRequestException;
import com.perficient.etm.exception.ResourceNotFoundException;
import com.perficient.etm.repository.GoalRepository;
import com.perficient.etm.repository.ReviewRepository;
import com.perficient.etm.security.SecurityUtils;

/**
 * REST controller for managing Goal.
 */
@RestController
@RequestMapping("/api")
public class GoalResource implements RestResource {

    private final Logger log = LoggerFactory.getLogger(GoalResource.class);

    @Inject
    private GoalRepository goalRepository;

    @Inject
    private ReviewRepository reviewRepository;

    /**
     * POST  /reviews/:reviewId/goals -> Create a new goal.
     * @throws URISyntaxException
     */
    @RequestMapping(value = "reviews/{reviewId}/goals",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional
    public ResponseEntity<Goal> create(@PathVariable Long reviewId, @RequestBody Goal goal, BindingResult result) throws URISyntaxException {
        log.debug("REST request to save Goal : {}", goal);
        if (result.hasErrors()) {
            throw new InvalidRequestException("Invalid goal", result);
        }
        Review review = reviewRepository.findOne(reviewId);
        SecurityUtils.getPrincipal().ifPresent(principal -> {
            if (review.isReviewee(principal) || review.isReviewer(principal)) {
                goal.setReview(review);
                goalRepository.save(goal);
            }
        });
        Goal savedGoal = new Goal(goal);
        savedGoal.setReview(null);
        return new ResponseEntity<>(savedGoal, HttpStatus.CREATED);
    }

    /**
     * PUT  review/:reviewId/goals -> Updates an existing goal.
     */
    @RequestMapping(value = "reviews/{reviewId}/goals",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@PathVariable Long reviewId, @RequestBody Goal goal, BindingResult result) throws URISyntaxException {
        log.debug("REST request to update Goal : {}", goal);
        Review review = reviewRepository.findOne(reviewId);
        SecurityUtils.getPrincipal().ifPresent(principal -> {
            if (review.isReviewer(principal)) {
                Goal currentGoal = goalRepository.findOne(goal.getId());
                // TODO: this logic might be different for AR
                // keep reviewee comment unchanged, save the rest
                goal.setEmployeeComment(currentGoal.getEmployeeComment());
                goalRepository.save(goal);
            }
            else if (review.isReviewee(principal)) {
                Goal currentGoal = goalRepository.findOne(goal.getId());
                // TODO: this logic might be different for AR
                // only change reviewee's comment
                currentGoal.setEmployeeComment(goal.getEmployeeComment());
                goalRepository.save(currentGoal);
            }
        });
        return ResponseEntity.ok().build();
    }

    /**
     * GET  reviews/:reviewId/goals -> get all the goals.
     */
    @RequestMapping(value = "reviews/{reviewId}/goals",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Goal> getAll(@PathVariable Long reviewId) {
        log.debug("REST request to get all Goals for review with id: {}", reviewId);
        return goalRepository.findAllByReviewId(reviewId);
    }

    /**
     * GET  review/:reviewId/goals/:id -> get the "id" goal.
     */
    @RequestMapping(value = "reviews/{reviewId}/goals/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Goal> get(@PathVariable Long id) {
        log.debug("REST request to get Goal : {}", id);
        return Optional.ofNullable(goalRepository.findOne(id))
            .map(goal -> new ResponseEntity<>(
                goal,
                HttpStatus.OK))
            .orElseThrow(() -> {
                return new ResourceNotFoundException("Goal " + id + " cannot be found.");
            });
    }

    /**
     * DELETE  review/:reviewId/goals/:id -> delete the "id" goal.
     * @return
     */
    @RequestMapping(value = "reviews/{reviewId}/goals/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> delete(@PathVariable Long reviewId, @PathVariable Long id) {
        log.debug("REST request to delete Goal : {}", id);
        Review review = reviewRepository.findOne(reviewId);
        SecurityUtils.getPrincipal().ifPresent(principal -> {
            if (review.isReviewee(principal)) {
                Goal goal = goalRepository.getOne(id);
                goalRepository.delete(goal);
            }
        });
        return ResponseEntity.ok().build();
    }
}
