package com.perficient.etm.service;

import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.perficient.etm.domain.Feedback;
import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.User;
import com.perficient.etm.exception.ResourceNotFoundException;
import com.perficient.etm.repository.FeedbackRepository;
import com.perficient.etm.repository.ReviewRepository;
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
    private UserService userSvc;

    @Inject
    private ReviewRepository reviewRepository;
    
    @Inject
    private ProcessService processSvc;
    
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
    	log.debug("Adding peer feedback for review {} and peer {}", reviewId, peer.getEmployeeId());
        Review review = reviewSvc.findById(reviewId);
    	Feedback f = new Feedback();
    	f.setAuthor(peer);
    	f.setReview(review);
    	// TODO: Set the feedback status to initiated/not sent
    	feedbackRepository.save(f);
    	review.getFeedback().add(f);
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
     * @throws ResourceNotFoundException 
     */
	public void removePeerFeedback(Long reviewId, Long peerId) {
		Optional<Feedback> feedback = reviewSvc.getFeedbackForPeer(reviewId, peerId);
		if (!feedback.isPresent()) {
			throw new ResourceNotFoundException("No peer review found for peer " + peerId + " on review " + reviewId);
		}
        processSvc.cancel(feedback.get().getFeedbackProcessId());
        // TODO: Set the feedback status to cancelled and save. Or does that happen by Activiti?
        Review review = reviewSvc.findById(reviewId);
        User peer = userSvc.getUser(peerId);
        review.getPeers().remove(peer);
        reviewSvc.update(review);
	}    
}
