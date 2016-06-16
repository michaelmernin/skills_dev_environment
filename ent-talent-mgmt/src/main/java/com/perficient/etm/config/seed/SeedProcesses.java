package com.perficient.etm.config.seed;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.transaction.annotation.Transactional;

import com.perficient.etm.domain.Feedback;
import com.perficient.etm.domain.FeedbackStatus;
import com.perficient.etm.domain.FeedbackType;
import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.ReviewStatus;
import com.perficient.etm.repository.ReviewRepository;
import com.perficient.etm.repository.UserRepository;
import com.perficient.etm.security.SecurityUtils;
import com.perficient.etm.service.FeedbackService;
import com.perficient.etm.service.ReviewService;
import com.perficient.etm.service.activiti.ProcessService;

public class SeedProcesses implements ApplicationListener<ContextRefreshedEvent> {

    @Inject
    private UserRepository userRepository;
    
    @Inject
    private ReviewRepository reviewRepository;
    
    @Inject
    private ReviewService reviewService;
    
    @Inject 
    private FeedbackService feedbackService;
    
    @Inject 
    private ProcessService processService;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        SecurityUtils.runAsSystem(userRepository, this::startAllReviewProcesses);
    }

    private void startAllReviewProcesses() {
        Stream<Review> reviewStream = reviewRepository.findAll().stream()
            .filter(review -> review.getProcessId() == null)
            .peek(reviewService::startReviewProcess)
            .peek(this::startAllPeerProcesses);

        completeTasksByStatus(reviewStream);
    }

    private void startAllPeerProcesses(Review review) {
        review.getPeers().stream()
            .map(peer -> feedbackService.addFeedback(review, peer, FeedbackType.PEER))
            .filter(feedback -> feedback.getFeedbackStatus() == FeedbackStatus.OPEN)
            .forEach(feedbackService::openPeerFeedback);
    }

    private void completeTasksByStatus(Stream<Review> reviewStream) {
        Map<ReviewStatus, List<Review>> reviewStatusMap = reviewStream
                .filter(review -> review.getReviewStatus() != ReviewStatus.OPEN)
                .collect(Collectors.groupingBy(Review::getReviewStatus));

        cancelProcesses(reviewStatusMap.get(ReviewStatus.CLOSED));
        // TODO completeTasks(ReviewStatus.DIRECTOR_APPROVAL, reviewStatusMap.get(ReviewStatus.DIRECTOR_APPROVAL));
        // TODO completeTasks(ReviewStatus.JOINT_APPROVAL, reviewStatusMap.get(ReviewStatus.JOINT_APPROVAL));
        // TODO completeTasks(ReviewStatus.GM_APPROVAL, reviewStatusMap.get(ReviewStatus.GM_APPROVAL));
        // TODO completeTasks(ReviewStatus.COMPLETE, reviewStatusMap.get(ReviewStatus.COMPLETE));
    }

    private void cancelProcesses(List<Review> reviews) {
        Optional.ofNullable(reviews).ifPresent(r -> {
            r.stream()
            .peek(review -> {
                processService.cancel(review.getProcessId());
            })
            .map(Review::getFeedback)
            .flatMap(Set::stream)
            .map(Feedback::getProcessId)
            .filter(Objects::nonNull)
            .forEach(processService::cancel);
        });
    }
}
