package com.perficient.etm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.perficient.etm.domain.Goal;
import com.perficient.etm.domain.Review;
import com.perficient.etm.exception.InvalidRequestException;
import com.perficient.etm.repository.GoalRepository;
import com.perficient.etm.repository.ReviewRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.transaction.Transactional;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Goal.
 */
@RestController
@RequestMapping("/api")
public class GoalResource {

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
    public ResponseEntity<Void> create(@PathVariable Long reviewId, @RequestBody Goal goal, BindingResult result) throws URISyntaxException {
        log.debug("REST request to save Goal : {}", goal);
        if (result.hasErrors()) {
        	throw new InvalidRequestException("Invalid goal", result);
        }
        if (goal.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new goal cannot already have an ID").build();
        }
        Review review = reviewRepository.getOne(reviewId);
        review.getGoals().add(goal);
        review = reviewRepository.save(review);
        goal.setReview(review);
        goalRepository.save(goal);
        return ResponseEntity.created(new URI("/api/reviews/" + reviewId + "/goals/" + goal.getId())).build();
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
        if (goal.getId() == null) {
            return create(reviewId, goal, result);
        }
        goalRepository.save(goal);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  reviews/:reviewId/goals -> get all the goals.
     */
    @RequestMapping(value = "reviews/{reviewId}/goals",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Goal> getAll() {
        log.debug("REST request to get all Goals");
        return goalRepository.findAll();
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
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  review/:reviewId/goals/:id -> delete the "id" goal.
     */
    @RequestMapping(value = "reviews/{reviewId}/goals",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long reviewId,@PathVariable Long id) {
        log.debug("REST request to delete Goal : {}", id);
        goalRepository.delete(id);
    }
}
