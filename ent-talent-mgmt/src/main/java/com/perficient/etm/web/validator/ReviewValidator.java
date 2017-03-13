package com.perficient.etm.web.validator;

import javax.inject.Inject;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.ReviewType;
import com.perficient.etm.repository.ReviewTypeRepository;
import com.perficient.etm.repository.UserRepository;

@WebValidator
public class ReviewValidator extends DomainValidator<Review> {

    @Inject
    ReviewTypeRepository reviewTypeRepository;

    @Inject
    UserRepository userRepository;
    
    ReviewType reviewType;

    @Override
    public void validateDomain(Review review, Errors errors) {
        boolean isAnnual = review.getReviewType().getProcessName().equals("annualReview");
        validateRequiredFields(review, errors);

        if (!errors.hasFieldErrors()) {
            validateEndDate(review, errors, isAnnual);
        }
    }

    private void validateEndDate(Review review, Errors errors, Boolean isAnnual) {
        if (isAnnual && review.getStartDate().plusYears(1).isAfter(review.getEndDate())) {
            errors.rejectValue("endDate", "min.review.endDate.year");
        }
        
        if (!isAnnual && review.getStartDate().plusDays(1).isAfter(review.getEndDate())) {
            errors.rejectValue("endDate", "min.review.endDate");
        }
    }

    private void validateRequiredFields(Review review, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "reviewType", "required");
        ValidationUtils.rejectIfEmpty(errors, "reviewee", "required");
        ValidationUtils.rejectIfEmpty(errors, "startDate", "required");
        ValidationUtils.rejectIfEmpty(errors, "endDate", "required");
    }

}
