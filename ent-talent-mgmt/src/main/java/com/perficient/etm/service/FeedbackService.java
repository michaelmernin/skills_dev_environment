package com.perficient.etm.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.perficient.etm.domain.Feedback;
import com.perficient.etm.domain.FeedbackStatus;
import com.perficient.etm.domain.FeedbackType;
import com.perficient.etm.domain.Rating;
import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.TodoResult;
import com.perficient.etm.domain.User;
import com.perficient.etm.repository.FeedbackRepository;
import com.perficient.etm.repository.RatingRepository;

@Service
@Transactional
public class FeedbackService extends AbstractBaseService {

    @Inject
    private PeerService peerService;
    
    @Inject
    private FeedbackRepository feedbackRepository;

    @Inject
    private RatingRepository ratingRepository;
    
    public Feedback addFeedback(Review review, User author, FeedbackType type) {
        return feedbackRepository.findOneByReviewIdAndAuthorId(review.getId(), author.getId())
            .orElseGet(() -> {
                Feedback feedback = new Feedback();
                feedback.setAuthor(author);
                feedback.setReview(review);
                feedback.setFeedbackStatus(initialFeedbackStatus(type));
                feedback.setFeedbackType(type);
                feedback = feedbackRepository.save(feedback);
                addRatings(review, feedback);
                return feedback;
            });
    }

    public Feedback openPeerFeedback(Feedback feedback) {
        if (feedback.getFeedbackType() == FeedbackType.PEER) {
            if (feedback.getProcessId() == null) {
                peerService.startPeerProcess(feedback);
            } else {
                peerService.completeTaskInFeedbackProcess(feedback, TodoResult.REJECT);
            }
        }
        return feedback;
    }

    public Feedback closeFeedback(Feedback feedback) {
        feedback.setFeedbackStatus(FeedbackStatus.CLOSED);
        return feedbackRepository.save(feedback);
    }
    
    public Feedback setStatusById(Long id, FeedbackStatus status) {
        return Optional.ofNullable(feedbackRepository.getOne(id))
            .map(f -> {
                f.setFeedbackStatus(status);
                return feedbackRepository.save(f);
            }).orElse(null);
    }

    private FeedbackStatus initialFeedbackStatus(FeedbackType type) {
        switch (type) {
        case PEER:
            return FeedbackStatus.NOT_SENT;
        case REVIEWER:
        case SELF:
            return FeedbackStatus.OPEN;
        default:
            return null;
        }
    }

    private void addRatings(Review review, final Feedback feedback) {
        if (feedback.getRatings().isEmpty()) {
            List<Rating> ratings = ratingRepository.save(getRatings(review, feedback));
            feedback.setRatings(new HashSet<>(ratings));
        }
    }

    private Set<Rating> getRatings(Review review, Feedback feedback) {
        return review.getReviewType().getQuestions().stream()
            .map(question -> {
                Rating rating = new Rating();
                rating.setQuestion(question);
                rating.setFeedback(feedback);
                return rating;
            }).collect(Collectors.toSet());
    }
}
