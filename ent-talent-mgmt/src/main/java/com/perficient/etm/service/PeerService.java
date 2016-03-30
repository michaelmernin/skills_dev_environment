package com.perficient.etm.service;

import java.util.Objects;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.perficient.etm.domain.Feedback;
import com.perficient.etm.domain.FeedbackStatus;
import com.perficient.etm.domain.FeedbackType;
import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.User;
import com.perficient.etm.exception.ActivitiProcessInitiationException;
import com.perficient.etm.repository.FeedbackRepository;
import com.perficient.etm.repository.UserRepository;
import com.perficient.etm.security.SecurityUtils;
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

    @Inject
    private UserRepository userRepository;
    
    @Inject
    private MailService mailService;
    
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
        SecurityUtils.runAsSystem(userRepository, ()->{
            Feedback feedback = null;
            feedback = feedbackService.addFeedback(review, peer, FeedbackType.PEER);
            review.getFeedback().add(feedback);
            review.getPeers().add(peer);
            reviewSvc.update(review);
        });
        return review;
    }

    /**
     * Starts the bpm process that is associated with a Feedback object in order to 
     * start tracking the progress of the feedback in the activiti engine.
     * @param feedback The Feedback object to start the process with
     * @return Feedback object updated with the corrected 
     * @throws ActivitiProcessInitiationException
     */
    public Feedback startPeerProcess(Feedback feedback) throws ActivitiProcessInitiationException{
        if (feedback.getFeedbackStatus() != null && !FeedbackStatus.NOT_SENT.equals(feedback.getFeedbackStatus())){
            throw new ActivitiProcessInitiationException(
                    new IllegalStateException("Unable to start Peer Porcess on a no Not_Sent status object"));
        }
        try{    
            String processId = processSvc.initiatePeerReview(feedback);
            //Update the needed information
            feedback.setProcessId(processId);
            feedback.setFeedbackStatus(FeedbackStatus.OPEN);
            feedbackRepository.save(feedback);
            //sending email to peer requisting feedback
        }catch (Exception e){
            log.error("Error starting peer bpm process",e);
            throw new ActivitiProcessInitiationException(e);
        }
        String email = (feedback.getAuthor() == null)? null : feedback.getAuthor().getEmail();
        if(email != null){
            mailService.sendPeerReviewFeedbackRequestedEmail(email);
        }
        return feedback;
    }
    
    public Feedback completeTaskInFeedbackProcess(Feedback feedback){
        processSvc.completePeerReviewTask(feedback, "TRUE");
        return feedback;
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
        SecurityUtils.runAsSystem(userRepository, ()->{
            feedbackRepository.findOneByReviewIdAndAuthorId(reviewId, peerId)
                .map(feedbackService::closeFeedback)
                .map(Feedback::getProcessId)
                .filter(Objects::nonNull)
                .map(processSvc::cancel);
        });
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
