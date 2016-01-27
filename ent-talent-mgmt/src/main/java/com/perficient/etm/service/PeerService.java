package com.perficient.etm.service;

import java.util.Objects;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.perficient.etm.domain.Feedback;
import com.perficient.etm.domain.FeedbackType;
import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.User;
import com.perficient.etm.repository.FeedbackRepository;
import com.perficient.etm.service.activiti.ProcessService;

/**
 * Service for managing peers and their contributions to reviews
 *
 * @author craigsmith
 *
 */
@Service
@Transactional
public class PeerService {
    private final Logger log = LoggerFactory.getLogger(PeerService.class);

    @Inject
    private ReviewService reviewSvc;

    @Inject
    private ProcessService processSvc;

    @Inject
    private FeedbackService feedbackService;
    
    @Inject
    private FeedbackRepository feedbackRepository;

    /**
     * Creates an empty feedback form, associates it with the review, assigns to the peer and
     * sets the status to initiated/not sent (?). The process isn't started for the feedback
     * yet.
     *
     * @param reviewId
     * @param peer
     * @return
     */
    public Review addPeerFeedback(Long reviewId, User peer) {
        log.debug("Adding peer feedback for review {} and peer {}", reviewId, peer.getId());
        Review review = reviewSvc.findById(reviewId);
        Feedback feedback = feedbackService.addFeedback(review, peer, FeedbackType.PEER);
        review.getFeedback().add(feedback);
        review.getPeers().add(peer);
        reviewSvc.update(review);
        return review;
    }

    /**
     * Retrieves the feedback from a peer on a review, stops the Activiti process for that peer feedback,
     * sets the status of the feedback to cancelled and removes it from the review
     *
     * @param reviewId
     * @param peerId
     */
    public void removePeerFeedback(Long reviewId, Long peerId) {
        removePeerFromReview(reviewId, peerId);

        feedbackRepository.findOneByReviewIdAndAuthorId(reviewId, peerId)
            .map(feedbackService::closeFeedback)
            .map(Feedback::getProcessId)
            .filter(Objects::nonNull)
            .map(processSvc::cancel);
    }

    private void removePeerFromReview(Long reviewId, Long peerId) {
        Review review = reviewSvc.findById(reviewId);
        boolean removed = review.getPeers().removeIf(peer -> {
            return peer.getId().equals(peerId); 
        });
        if (removed) {
            reviewSvc.update(review);
        }
    }
}
