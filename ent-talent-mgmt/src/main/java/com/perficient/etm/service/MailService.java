package com.perficient.etm.service;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import com.perficient.etm.domain.User;
import com.perficient.etm.repository.UserRepository;
import com.perficient.etm.security.SecurityUtils;

/**
 * Service for sending e-mails.
 * <p/>
 * <p>
 * We use the @Async annotation to send e-mails asynchronously.
 * </p>
 */
@Service
public class MailService {

    private final Logger log = LoggerFactory.getLogger(MailService.class);

    @Inject
    private Environment env;

    @Inject
    private JavaMailSenderImpl javaMailSender;

    @Inject
    private MessageSource messageSource;

    @Inject
    private SpringTemplateEngine templateEngine;
    
    @Inject
    private UserService userService;
    
    @Inject
    private ReviewService reviewService;
    
    @Inject
    private UserRepository userRepository;

    /**
     * System default email address that sends the e-mails.
     */
    private String from;
    
    private String baseUrl;
    
    private String profile;

    @PostConstruct
    public void init() {
        this.from = env.getProperty("spring.mail.from");
        this.baseUrl = env.getProperty("spring.mail.baseUrl");
        this.profile = env.getProperty("spring.profiles.active");
    }

    /**
     * Sends  an email using the given parameters
     * @param to the email to send to
     * @param subject email subject
     * @param content Content of the email html/text
     * @param isMultipart boolean for multipart emails with assets
     * @param isHtml if content is html or not (text)
     */
    @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug("Send e-mail[multipart '{}' and html '{}'] to '{}' with subject '{}'",
                isMultipart, isHtml, to, subject);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
            message.setTo(to);
            message.setFrom(from);
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.debug("Sent e-mail to '{}'", to);
        } catch (Exception e) {
            log.warn("E-mail could not be sent to user '{}', exception is: {}", to, e.getMessage());
        }
    }
    
    /**
     * Sends an email to ONLY to an existing user in the userRepository, using their id
     * @param recipientId the id of the user in the repository to whom the email is sent
     * @param reviewId the review id this email is about (can be null)
     * @param subject Subject of the Email or Spring Message in MessageSource
     * @param EmailTemplate Email template name for thymleafe template engine to use
     * @param locale the locale thymeleaf will use for translation and localization
     * @throws MessagingException
     */
    public void sendEmail(final Long recipientId, final Long reviewId, String subject, String EmailTemplate, Map<String, Object> contextMap){
        User recipientUser = userService.getUser(recipientId);
        // if the recipient user does not exist, return.
       Optional<User> optUser = Optional.ofNullable(recipientUser);
       if(!optUser.isPresent()){
    	   log.error("Cannot send email to usee with Id: {}, user does not exist", recipientId);
           return;
       }
       optUser.ifPresent(user -> {
    	   // adds some user info (first name, last name, title ...etc) to the context
           Locale locale = Locale.forLanguageTag(Locale.US.getLanguage());
           Context context = new Context(locale);
           String recipientEmail = user.getEmail();
           // add contextVars to context
           Optional.ofNullable(contextMap).ifPresent(ctxmap ->{
        	   ctxmap.forEach((key, value) -> context.setVariable(key, value));
           });
           context.setVariable(EmailConstants.BASE_URL, baseUrl);
           context.setVariable(EmailConstants.PROFILE, profile);
           // add user and review vars to context
           addUserInfoToContext(context, user);
           addReviewInfoToContext(context, reviewId);
           // Create the HTML body using Thymeleaf
           final String htmlContent = this.templateEngine.process(EmailTemplate, context);
           // try to resolve 
           String subj="";
           try{
        	   subj = messageSource.getMessage(subject, null, locale);
           }catch(NoSuchMessageException e){
        	   subj = subject;
           }finally{
        	   sendEmail(recipientEmail, subj, htmlContent, false, true);
           }
       });
    }
    
    /**
     * Adds user info variables to context (to be used in thymeleaf email templates)
     * @param context
     * @param user
     * @return
     */
    private Context addUserInfoToContext(Context context, User user){
        context.setVariable(EmailConstants.User.FIRST_NAME, user.getFirstName());
        context.setVariable(EmailConstants.User.LAST_NAME, user.getLastName());
        context.setVariable(EmailConstants.User.FULL_NAME, user.getFullName());
        context.setVariable(EmailConstants.User.ID, user.getId());
        context.setVariable(EmailConstants.User.EMAIL, user.getEmail());
        context.setVariable(EmailConstants.User.LOGIN, user.getLogin());
        context.setVariable(EmailConstants.User.TITLE, user.getTitle());
        context.setVariable(EmailConstants.User.TARGET_TITLE, user.getTargetTitle());
        return context;
    }
    
    /**
     * Adds review info variables to context (to be used in thymeleaf email templates)
     * @param context
     * @param reviewId
     * @return
     */
    private Context addReviewInfoToContext(Context context, Long reviewId ) {
        SecurityUtils.runAsSystem(userRepository, () -> {
            Optional.ofNullable(reviewId).ifPresent(rId -> {
                Optional.ofNullable(reviewService.findById(rId)).ifPresent(review -> {
                    context.setVariable(EmailConstants.Review.ID, rId);
                    Optional.ofNullable(review.getReviewType()).ifPresent(revieweType -> {
                        context.setVariable(EmailConstants.Review.TYPE, revieweType.getName());
                    });
                    Optional.ofNullable(review.getReviewStatus()).ifPresent(revieweStatus -> {
                        context.setVariable(EmailConstants.Review.STATUS, revieweStatus.getName());
                    });
                    Optional.ofNullable(review.getReviewee()).ifPresent(reviewee -> {
                        context.setVariable(EmailConstants.Review.REVIEWEE_FIRST_NAME, reviewee.getFirstName());
                        context.setVariable(EmailConstants.Review.REVIEWEE_LAST_NAME, reviewee.getLastName());
                        context.setVariable(EmailConstants.Review.REVIEWEE_FULL_NAME, reviewee.getFullName());
                        context.setVariable(EmailConstants.Review.REVIEWEE_ID, reviewee.getId());
                    });
                    Optional.ofNullable(review.getReviewer()).ifPresent(reviewer -> {
                        context.setVariable(EmailConstants.Review.REVIEWER_FIRST_NAME, reviewer.getFirstName());
                        context.setVariable(EmailConstants.Review.REVIEWER_FULL_NAME, reviewer.getFullName());
                        context.setVariable(EmailConstants.Review.REVIEWER_FULL_NAME, reviewer.getFullName());
                        context.setVariable(EmailConstants.Review.REVIEWER_ID, reviewer.getId());
                    });
                });
            });
        });
		return context;
    }
   
    @Async
    public void sendActivationEmail(Long userId) {
    	log.debug("Sending activation e-mail to user with id: '{}'", userId);
        sendEmail(userId, null, EmailConstants.Subjects.ACTIVATION, EmailConstants.Templates.ACTIVATION, null);
    }

    
    /** Sends an email to user with userId and includes reviewId for the review URL. Intended for use with activinti.
     * @param userId
     * @param reviewId
     */
    public void sendAnnualReviewStartedEmail(Long userId, Long reviewerId, Long reviewId) {
        log.debug("Sending annual review process started e-mail to '{}'");
        sendEmail(userId, reviewId, EmailConstants.Subjects.ANNULA_REVIEW_STARTED, EmailConstants.Templates.REVIEW_STARTED, null);
        sendEmail(reviewerId, reviewId, EmailConstants.Subjects.ANNULA_REVIEW_STARTED, EmailConstants.Templates.REVIEW_STARTED, null);
    }

    /**
     * Sends a reminder email, to peer with userId, to remind them to give feedback on review with reviewId 
     * @param userId the user id to send an email to
     * @param reviewId the review id the email is about
     */
    public void sendPeerFeedbackReminderEmail(Long userId, Long reviewId) {
        log.debug("Sending peer feedback process reminder e-mail to '{}'");
        sendEmail(userId, reviewId, EmailConstants.Subjects.PEER_FEEDBACK_REMINDER, EmailConstants.Templates.PEER_FEEDBACK_REMINDER, null);
    }
    
    
    /**
     * Sends a request for feedback, on review with reviewId, to a peer with peerId
     * @param peerId the peerId used to send the email
     * @param reviewId the reviewId on which the peer needs to submit feedback
     */
    public void sendPeerFeedbackRequestedEmail(Long peerId, Long reviewId) {
        log.debug("Sending peer feedback requested e-mail to '{}'");
        sendEmail(peerId, reviewId, EmailConstants.Subjects.PEER_FEEDBACK_REQUESTED, EmailConstants.Templates.PEER_FEEDBACK_REQUESTED, null);
    }
    
    public void sendPeerFeedbackSubmittedEmail(Long peerId, Long reviewId) {
        log.debug("Sending peer review submitted e-mail to '{}'");
        sendEmail(peerId, reviewId, EmailConstants.Subjects.PEER_FEEDBACK_REQUESTED, EmailConstants.Templates.PEER_FEEDBACK_REQUESTED, null);
    }
    
    
    
}
