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
        boolean isAnnual = !review.getReviewType().getProcessName().equals("annualReview");
        validateRequiredFields(review, errors, isAnnual);

        if (!errors.hasFieldErrors() && isAnnual) {
            validateEndDate(review, errors);
        }
    }

    private void validateEndDate(Review review, Errors errors) {
        if (review.getStartDate().plusYears(1).isAfter(review.getEndDate())) {
            errors.rejectValue("endDate", "min");
        }
    }

    private void validateRequiredFields(Review review, Errors errors, boolean isAnnual) {
        ValidationUtils.rejectIfEmpty(errors, "reviewType", "required");
        ValidationUtils.rejectIfEmpty(errors, "reviewee", "required");
        if (isAnnual) {
            ValidationUtils.rejectIfEmpty(errors, "startDate", "required");
            ValidationUtils.rejectIfEmpty(errors, "endDate", "required");
        }
    }

}
