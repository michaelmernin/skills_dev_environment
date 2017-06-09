package com.perficient.etm.notification;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.ReviewStatus;
import com.perficient.etm.domain.User;
import com.perficient.etm.repository.UserRepository;
import com.perficient.etm.service.MailService;

@Component
public class NotificationScheduler {

    private final Logger log = LoggerFactory.getLogger(NotificationScheduler.class);
    private static final String ENV_NOTIFICATION_AR = "etm.notification.annualreview.";
    private static final String PROP_DAYS_BEFORE_REVIEW_DELAYED = ENV_NOTIFICATION_AR+"reminderDaysBeforeReviewIsDelayed";
    private static final String PROP_DAYS_AFTER_REVIEW_DELAYED = ENV_NOTIFICATION_AR+"reminderDaysAfterReviewIsDelayed";

    @Inject
    private UserRepository userRepository;
    
    @Inject
    private MailService mailService;


    @Value("${"+PROP_DAYS_BEFORE_REVIEW_DELAYED+"}")
    private int reminderDaysBeforeReviewIsDelayed;

    @Value("${"+PROP_DAYS_AFTER_REVIEW_DELAYED+"}")
    private int reminderDaysAfterReviewIsDelayed;

    @Scheduled(cron = "0 1 1 * * ?")
    public void notifyUsersForAnnualReview() {
        List<User> users = getAll();
        users.stream().forEach(user -> {
            LocalDate now = LocalDate.now();
            Review currentReview = getCurrentAnnualReview(user);
            List<Review> previousReviews = getPreviousAnnualReviews(user);
            if (currentReview != null) {
                LocalDate endReviewDateMinusNumberOfDays = currentReview.getEndDate().minusDays(reminderDaysBeforeReviewIsDelayed);

                //Review Completion
                if (endReviewDateMinusNumberOfDays.equals(now) && currentReview.getReviewStatus().getId() < ReviewStatus.COMPLETE.getId()) {
                    //send email to Reviewer and Reviewee
                    mailService.sendNotificationEmailForReviewCompletion(user.getId(), user);
                    if (user.getCounselor() != null) {
                        mailService.sendNotificationEmailForReviewCompletion(user.getCounselor().getId(), user);
                    }
                }
            } else {
                //Review Start
                mailService.sendNotificationEmailForReviewStart(user.getId(), user);
                if (user.getCounselor() != null) {
                    mailService.sendNotificationEmailForReviewStart(user.getCounselor().getId(), user);
                }
            }


            previousReviews.forEach(previousReview -> {

                LocalDate endReviewDate = previousReview.getEndDate();
                int days = Days.daysBetween(now, endReviewDate).getDays();

                //Review Late
                if ((endReviewDate.equals(now) || (days % reminderDaysAfterReviewIsDelayed == 0)) && previousReview.getReviewStatus().getId() != ReviewStatus.COMPLETE.getId()) {
                    //send mail to Review/Reviewee/Director/Counselor
                    mailService.sendNotificationEmailForReviewLate(user.getId(), user);
                    if (user.getCounselor() != null) {
                        mailService.sendNotificationEmailForReviewLate(user.getCounselor().getId(), user);
                    }
                    if (user.getDirector() != null) {
                        mailService.sendNotificationEmailForReviewLate(user.getDirector().getId(), user);
                    }
                    if (user.getGeneralManager() != null) {
                        mailService.sendNotificationEmailForReviewLate(user.getGeneralManager().getId(), user);
                    }
                }
            });
        });
    }

    List<User> getAll() {
        log.debug("REST request to get all normal Users");
        return userRepository.findAllNormalUsers();
    }
    
    Review getCurrentAnnualReview(User user){
        LocalDate now = LocalDate.now();
        Set<Review> reviews = user.getSelfReviews();
        
        for(Review review:reviews){
            if((review.getReviewType().getId() == 1)){
                if(review.getStartDate().isBefore(now) && review.getEndDate().isAfter(now)){
                   return review;
                }
            }
        }
        return null;
    }
    
    List<Review> getPreviousAnnualReviews(User user){
        LocalDate now = LocalDate.now();
        Set<Review> reviews = user.getSelfReviews();
        List<Review> previousReviews = new ArrayList<Review>();
        
        for(Review review:reviews){
            if((review.getReviewType().getId() == 1) && review.getEndDate().isBefore(now)){
               previousReviews.add(review);
            }
        }
        return previousReviews;
    }

}