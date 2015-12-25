package com.perficient.etm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.perficient.etm.domain.Feedback;
import com.perficient.etm.domain.Review;
import com.perficient.etm.exception.InvalidRequestException;
import com.perficient.etm.repository.FeedbackRepository;
import com.perficient.etm.repository.RatingRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Feedback for a Review.
 */
@RestController
@RequestMapping("/api")
public class FeedbackResource {

	private final Logger log = LoggerFactory.getLogger(FeedbackResource.class);

	@Inject
	private FeedbackRepository feedbackRepository;

	@Inject
	private RatingRepository ratingRepository;

	/**
	 * POST /reviews/:id/feedback -> Create a new feedback.
	 */
	@RequestMapping(value = "/reviews/{reviewId}/feedback", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Feedback> create(@PathVariable Long reviewId, @RequestBody Feedback feedback,
			BindingResult result) {
		log.debug("REST request to save Feedback : {} for Review : {}", feedback, reviewId);
		if (result.hasErrors()) {
			throw new InvalidRequestException("Invalid new feedback", result);
		}
		if (feedback.getReview() == null) {
			feedback.setReview(new Review());
		}
		if (feedback.getReview().getId() == null) {
			feedback.getReview().setId(reviewId);
		}
		feedbackRepository.save(feedback);
		feedback.getRatings().stream().forEach((r) -> {
			r.setFeedback(feedback);
		});
		ratingRepository.save(feedback.getRatings());
		return new ResponseEntity<>(feedback, HttpStatus.CREATED);
	}

	/**
	 * PUT /reviews/:id/feedback -> Updates an existing feedback.
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
		if (feedback.getReview() == null) {
			feedback.setReview(new Review());
		}
		if (feedback.getReview().getId() == null) {
			feedback.getReview().setId(reviewId);
		}
		feedbackRepository.save(feedback);
		return ResponseEntity.ok().build();
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
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
}
