package com.perficient.etm.notification;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
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

@Component
public class NotificationScheduler {

    private final Logger log = LoggerFactory.getLogger(NotificationScheduler.class);
    
    @Inject
    private UserRepository userRepository;
    
    @Inject
    private MailService mailService;

    @Scheduled(cron = "0 1 1 * * ?")
    public void notifyUsersForAnnualReview() {
        
        Properties p = loadProperties();
        int reminderDaysBeforeReviewIsDelayed = Integer.parseInt(p.getProperty("annualReview.reminderDaysBeforeReviewIsDelayed"));
        int reminderDaysAfterReviewIsDelayed = Integer.parseInt(p.getProperty("annualReview.reminderDaysAfterReviewIsDelayed"));
        
        List<User> users = getAll();
        users.stream().forEach(user ->{
           LocalDate now = LocalDate.now();
           Review currentReview = getCurrentAnnualReview(user);
           List<Review> previousReviews = getPreviousAnnualReviews(user);
           if(currentReview !=null){
               LocalDate endReviewDateMinusNumberOfDays = currentReview.getEndDate().minusDays(reminderDaysBeforeReviewIsDelayed);
               
               //Review Completion         
               if(endReviewDateMinusNumberOfDays.equals(now) && currentReview.getReviewStatus().getId() < ReviewStatus.COMPLETE.getId()){
                   //send email to Reviewer and Reviewee
                   mailService.sendNotificationEmailForReviewCompletion(user.getId(),user);
                   if(user.getCounselor() != null){
                       mailService.sendNotificationEmailForReviewCompletion(user.getCounselor().getId(),user);
                   }
               }        
           }else{ 
               //Review Start
               mailService.sendNotificationEmailForReviewStart(user.getId(),user);
               if(user.getCounselor() != null){
                   mailService.sendNotificationEmailForReviewStart(user.getCounselor().getId(),user);
               }   
           }
        
           
           previousReviews.forEach(previousReview -> {
               
               LocalDate endReviewDate = previousReview.getEndDate();
               int days = Days.daysBetween(now, endReviewDate).getDays();
               
               //Review Late
               if((endReviewDate.equals(now) || (days % reminderDaysAfterReviewIsDelayed == 0 )) && previousReview.getReviewStatus().getId() != ReviewStatus.COMPLETE.getId()){
                   //send mail to Review/Reviewee/Director/Counselor
                   mailService.sendNotificationEmailForReviewLate(user.getId(),user);
                   if(user.getCounselor() != null){
                       mailService.sendNotificationEmailForReviewLate(user.getCounselor().getId(),user);
                   }
                   if(user.getDirector() != null){
                       mailService.sendNotificationEmailForReviewLate(user.getDirector().getId(),user);
                   }
                   if(user.getGeneralManager() != null){
                       mailService.sendNotificationEmailForReviewLate(user.getGeneralManager().getId(),user);
                   }
               }
           });
        });
    }
    
    public Properties loadProperties() {
        Properties prop = new Properties();
        try {
          //  prop.load(getClass().getResourceAsStream("src/main/resorces/notificationConfiguration.properties"));//here your src folder
            
            prop.load(new FileReader(new File("src/main/resources/notificationConfiguration.properties")));
        } catch (FileNotFoundException e) {
            log.error("NotificationConfiguration properties file not found");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
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