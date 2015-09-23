package com.perficient.etm.web.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.perficient.etm.domain.Review;
import com.perficient.etm.exception.InvalidRequestException;
import com.perficient.etm.repository.ReviewRepository;
import com.perficient.etm.web.validator.ReviewValidator;

/**
 * REST controller for managing Review.
 */
@RestController
@RequestMapping("/api")
public class ReviewResource {

    private final Logger log = LoggerFactory.getLogger(ReviewResource.class);

    @Inject
    private ReviewRepository reviewRepository;
    
    @Inject
    private ReviewValidator reviewValidator;
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(reviewValidator);
    }

    /**
     * POST  /reviews -> Create a new review.
     */
    @RequestMapping(value = "/reviews",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Review> create(@Valid @RequestBody Review review, BindingResult result) {
        log.debug("REST request to create Review : {}", review);
        if (result.hasErrors()) {
            throw new InvalidRequestException("Invalid new review", result);
        }
        review.sanitize(true);
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
     * PUT  /reviews/:id -> Update a review.
     */
    @RequestMapping(value = "/reviews/{id}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Review> update(@Valid @RequestBody Review review, BindingResult result) {
        log.debug("REST request to update Review : {}", review);
        if (result.hasErrors()) {
            throw new InvalidRequestException("Invalid review update", result);
        }
        review.sanitize(false);
        review = reviewRepository.save(review);
        return new ResponseEntity<>(review, HttpStatus.OK);
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
    
    /**
     * GET  /reviews/:id/engagements -> get the engagements in the timeframe of "id" review.
     */
    @RequestMapping(value = "/reviews/{id}/engagements",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Review> getEngagementsForAnnualReviewWithId(@PathVariable Long id) {
    	Review review = reviewRepository.findOne(id);
        //return reviewRepository.findAllEngagementsWithinAnnualReviewOfUser(review.getReviewee().getId(), review.getStartDate(), review.getEndDate());
    	List<Review> enagagements = new ArrayList<Review>();
    	Review engagement = new Review();
    	engagement.setRole("Technical Whosit");
    	engagement.setProject("A Tech Project");
    	engagement.setClient("Some Client Name");
    	engagement.setResponsibilities("Such and such responsibilities. This is usually pretty long. We have lots of responsibilitites.");
    	engagement.setRating(3.25);
    	engagement.setStartDate(review.getStartDate().plusMonths(1));
    	engagement.setEndDate(review.getEndDate().minusMonths(1));
        enagagements.add(engagement);
        engagement = new Review();
        engagement.setRole("Business Whatsit");
        engagement.setProject("A Business Project");
        engagement.setClient("Another Client Name");
        engagement.setResponsibilities("Such and such responsibilities. This is usually pretty long. We have lots of responsibilitites. With another sentence.");
        engagement.setRating(4.0);
        engagement.setStartDate(review.getStartDate().minusMonths(1));
        engagement.setEndDate(review.getEndDate().minusMonths(2));
        enagagements.add(engagement);
    	return enagagements;
    }
}
