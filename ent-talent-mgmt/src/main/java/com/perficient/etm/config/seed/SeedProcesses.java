package com.perficient.etm.config.seed;

import javax.inject.Inject;

import org.springframework.beans.factory.InitializingBean;

import com.perficient.etm.domain.ReviewStatus;
import com.perficient.etm.repository.ReviewRepository;
import com.perficient.etm.service.ReviewService;

public class SeedProcesses implements InitializingBean {

    @Inject
    private ReviewService reviewService;
    
    @Inject
    private ReviewRepository reviewRepository;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        reviewRepository.findAllByReviewStatus(ReviewStatus.OPEN).stream()
            .forEach(review -> {
                reviewService.startReviewProcess(review);
            });
    }
}
