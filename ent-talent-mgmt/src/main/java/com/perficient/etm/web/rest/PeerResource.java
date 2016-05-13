package com.perficient.etm.web.rest;

import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import com.perficient.etm.domain.Feedback;
import com.perficient.etm.domain.User;
import com.perficient.etm.repository.FeedbackRepository;
import com.perficient.etm.service.PeerService;
import com.perficient.etm.service.UserService;
import com.perficient.etm.web.view.View;

/**
 * REST controller for managing Review.
 */
@RestController
@RequestMapping("/api")
public class PeerResource implements RestResource {

    private final Logger log = LoggerFactory.getLogger(PeerResource.class);

    @Inject
    private PeerService peerSvc;
    
    @Inject
    private FeedbackRepository feedbackRepository;
    
    @Inject
    private UserService userService;
    
    /**
     * POST  /reviews/:reviewId/peers -> Add peers to a review
     * @throws URISyntaxException
     */
    @RequestMapping(value = "reviews/{reviewId}/peers/addPeer/{id}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional
    public ResponseEntity<Feedback> save(@PathVariable Long reviewId, @PathVariable Long id) throws URISyntaxException {
        log.debug("REST request to update peers for Review Id : {}", reviewId);
        User peer = userService.getUser(id);
        peerSvc.addPeerFeedback(reviewId, peer);
        List<Feedback> feedbacks = feedbackRepository.findAllByReviewIdAndFeedbackType(reviewId);
        Feedback newFeedback = new Feedback();
        for (int i = 0; i < feedbacks.size(); i++) {
            Feedback compareFeedback = feedbacks.get(i);
            if (compareFeedback.getAuthor().getId() == id) {
                newFeedback = compareFeedback;
            }
        }
        return new ResponseEntity<>(newFeedback, HttpStatus.CREATED);
    }
    
    /**
     * GET  /reviews/:reviewId/peers -> get all the peers of a review.
     */
    @RequestMapping(value = "/reviews/{reviewId}/peers",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @JsonView(View.Public.class)
    public List<Feedback> getAll(@PathVariable Long reviewId) {
        return feedbackRepository.findAllByReviewIdAndFeedbackType(reviewId);
    }

    /**
     * DELETE  /reviews/:reviewId/peers -> Remove peers from a review
     * @throws URISyntaxException
     */
    @RequestMapping(value = "/reviews/{reviewId}/peers/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long reviewId, @PathVariable Long id) throws URISyntaxException {
        log.debug("REST request to delete peer feedback for peer {} on review Id {}", id, reviewId);
        peerSvc.removePeerFeedback(reviewId, id);
        return ResponseEntity.ok().build();
    }
}
