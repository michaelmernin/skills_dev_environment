package com.perficient.etm.web.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
import com.fasterxml.jackson.annotation.JsonView;
import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.ReviewType;
import com.perficient.etm.exception.ETMException;
import com.perficient.etm.exception.InvalidRequestException;
import com.perficient.etm.exception.ResourceNotFoundException;
import com.perficient.etm.exception.ReviewProcessNotFound;
import com.perficient.etm.repository.ReviewTypeRepository;
import com.perficient.etm.service.ReviewService;
import com.perficient.etm.web.validator.ReviewValidator;
import com.perficient.etm.web.view.View;

/**
 * REST controller for managing Review.
 */
@RestController
@RequestMapping("/api")
public class ReviewResource implements RestResource {

    private final Logger log = LoggerFactory.getLogger(ReviewResource.class);
    private final boolean useDefaultDates = true;


    @Value("${etm.er.startdate:}")
    private String defaultErStartDate;

    @Value("${etm.er.enddate:}")
    private String defaultErEndtDate;

    @Inject
    private ReviewValidator reviewValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(reviewValidator);
    }

    @Inject
    private ReviewService reviewSvc;
    
    @Inject
    private ReviewTypeRepository reviewTypeRepository;

    /**
     * POST  /reviews -> Create a new review.
     * @throws ReviewProcessNotFound If the type specified in the Review.getReviewType method has
     * not been registered in the back-end services.
     */
    @RequestMapping(value = "/reviews",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Review> create(@Valid @RequestBody Review review, BindingResult result) throws ETMException {
        log.debug("REST request to create Review : {}", review);
        if (result.hasErrors()) {
            throw new InvalidRequestException("Invalid new review", result);
        }
        if(useDefaultDates){
            review = setDefaultDates(review);
        }
        review.sanitize();
        review = getReviewSvc().startReviewProcess(review);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
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
        return getReviewSvc().findAll();
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
        return Optional.ofNullable(getReviewSvc().findById(id))
            .map(review -> new ResponseEntity<>(
                review,
                HttpStatus.OK))
            .orElseThrow(() -> {
                return new ResourceNotFoundException("Review " + id + " cannot be found.");
            });
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
        getReviewSvc().delete(id);
    }

    /**
     * GET  /reviews/:id/engagements -> get the engagements in the timeframe of a review.
     */
    @RequestMapping(value = "/reviews/{id}/engagements",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Review> getEngagements(@PathVariable Long id) {
        Review review = getReviewSvc().findById(id);
        //return reviewRepository.findCompletedEngagementsForUserWithinDates(review.getReviewee().getId(), review.getStartDate(), review.getEndDate());
        List<Review> enagagements = new ArrayList<Review>();
        Review engagement = new Review();
        engagement.setRole("Technical Whosit");
        engagement.setClient("Some Client Name");
        engagement.setResponsibilities("Such and such responsibilities. This is usually pretty long. We have lots of responsibilitites.");
        engagement.setRating(3.25);
        engagement.setStartDate(review.getStartDate().plusMonths(1));
        engagement.setEndDate(review.getEndDate().minusMonths(1));
        enagagements.add(engagement);
        engagement = new Review();
        engagement.setRole("Business Whatsit");
        engagement.setClient("Another Client Name");
        engagement.setResponsibilities("Such and such responsibilities. This is usually pretty long. We have lots of responsibilitites. With another sentence.");
        engagement.setRating(4.0);
        engagement.setStartDate(review.getStartDate().minusMonths(1));
        engagement.setEndDate(review.getEndDate().minusMonths(2));
        enagagements.add(engagement);
        return enagagements;
    }

    public ReviewService getReviewSvc() {
        return reviewSvc;
    }

    public void setReviewSvc(ReviewService reviewSvc) {
        this.reviewSvc = reviewSvc;
    }
    
    /**
     * GET  start date of annual review that will be created.
     */
    @RequestMapping(value = "/reviews/get/reviews",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @JsonView(View.Public.class)
    List<Review> getReviewsByTypeAndReviewee(Long revieweeId, Long reviewTypeId) {
        log.debug("REST request to get list of reviews by reviewee and review type");
        ReviewType reviewType = reviewTypeRepository.findOne((long)reviewTypeId);
        return getReviewSvc().findAllByReviewTypeAndRevieweeId(reviewType, revieweeId);
    }

    private Review setDefaultDates (Review review){
        if (review == null) return review;
        List<Integer> startDateList = getDateList(defaultErStartDate);
        if (startDateList.size() == 3){
            review.setStartDate(new LocalDate(startDateList.get(2),startDateList.get(0), startDateList.get(1)));
        }

        List<Integer> erEndtDateList = getDateList(defaultErEndtDate);
        if (erEndtDateList.size() == 3){
            review.setEndDate(new LocalDate(erEndtDateList.get(2),erEndtDateList.get(0), erEndtDateList.get(1)));
        }

        return review;
    }

    /**
     * get date as a list from string matching MM/DD/YYY
     * @param date
     * @return
     */
    private List<Integer> getDateList(String date){
        return Optional.ofNullable(date)
                .map(startDateStr -> startDateStr.split("/"))
                .map(Arrays::stream)
                .map(arrayStream ->
                     arrayStream.map(Integer::parseInt)
                     .collect(Collectors.toList()))
                .orElse(null);
    }
}
