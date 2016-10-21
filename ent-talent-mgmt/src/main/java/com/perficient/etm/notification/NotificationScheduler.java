package com.perficient.etm.notification;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.ReviewStatus;
import com.perficient.etm.domain.User;
import com.perficient.etm.repository.UserRepository;
import com.perficient.etm.service.MailService;
import com.perficient.etm.web.rest.UserResource;

@Component
public class NotificationScheduler {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private static final int reminderDaysbeforeAnniversary = 14;

    private final Logger log = LoggerFactory.getLogger(NotificationScheduler.class);

    @Inject
    private UserRepository userRepository;
    
    @Inject
    private MailService mailService;

    @Scheduled(fixedRate = 50000)
    public void notifyUsersForAnnualReview() {
        
        List<User> users = getAll();
        users.stream().forEach(user ->{
           LocalDate startdate =  user.getStartDate();
           LocalDate now = LocalDate.now();
         
           
           //Review Start
           if(startdate.equals(now)){
               mailService.sendNotificationEmailForReviewStart(user.getId(),null);
               mailService.sendNotificationEmailForReviewStart(user.getCounselor().getId(),user);
           }
           
           Review currentReview = getCurrentAnnualReview(user);
           LocalDate endReviewDate = currentReview.getEndDate();
           LocalDate endReviewDateMinusTwoMonths = endReviewDate.minusDays(reminderDaysbeforeAnniversary);
           String currentReviewStatus = currentReview.getReviewStatus().getName();
           int days = Days.daysBetween(now, endReviewDate).getDays();
           
           //Review Completion         
           if(endReviewDateMinusTwoMonths.equals(now) && getCurrentAnnualReview(user).getReviewStatus().getId() < ReviewStatus.COMPLETE.getId()){
               //send email to Reviewer and Reviewee
               mailService.sendNotificationEmailForReviewCompletion(user.getId());
               mailService.sendNotificationEmailForReviewCompletion(user.getCounselor().getId());
           }   
           
           //Review Late
           if((endReviewDate.equals(now) || (days % reminderDaysbeforeAnniversary == 0 )) && currentReviewStatus != "COMPLETE"){
               //send mail to Review/Reviewee/Director/Counselor
               mailService.sendNotificationEmailForReviewLate(user.getId());
               mailService.sendNotificationEmailForReviewLate(user.getCounselor().getId());
               mailService.sendNotificationEmailForReviewLate(user.getDirector().getId());
               mailService.sendNotificationEmailForReviewLate(user.getGeneralManager().getId());
           }
           
        });
       
    }
    
    List<User> getAll() {
        log.debug("REST request to get all Users");
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

}