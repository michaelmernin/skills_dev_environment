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
import com.perficient.etm.repository.ReviewRepository;

/**
 * Service for high level review functionality.
 * 
 * @author craigsmith
 *
 */
@Service
@Transactional
public class ReviewService {
    private final Logger log = LoggerFactory.getLogger(ReviewService.class);

    @Inject
	private ReviewRepository reviewRepo;
    
    @Inject
    private UserService userSvc;
	
	public Optional<Feedback> getFeedbackForPeer(Long reviewId, Long peerId) {
        Review review = reviewRepo.getOne(reviewId);
        User peer = userSvc.getUser(peerId);
        Optional<Feedback> feedback = 
        		review.getFeedback().stream().filter(f->{return f.belongsToAuthor(peer);}).findFirst();
		return feedback;
	}

	public Review getReview(Long reviewId) {
		return reviewRepo.getOne(reviewId);
	}

	public void update(Review review) {
		reviewRepo.save(review);
	}

}
