package com.perficient.etm.service;

import java.util.HashMap;
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
import org.springframework.beans.factory.annotation.Value;
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

    /**
     * System default email address that sends the e-mails.
     */
    private String from;

    @PostConstruct
    public void init() {
        this.from = env.getProperty("spring.mail.from");
    }

    /**
     * Uses 
     * @param to
     * @param subject
     * @param content
     * @param isMultipart
     * @param isHtml
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
     * Sends an email to ONLY to an existing user in the userRepository
     * @param recipientEmail the 'to' email
     * @param subject Subject of the Email or Spring Message in MessageSource
     * @param EmailTemplate Email template name for thymleafe template engine to use
     * @param locale 
     * @throws MessagingException
     */
    public void sendEmail(final String recipientEmail,String subject, String EmailTemplate, Map<String, Object> contextMap){
        User recipientUser = userService.getUserByEmail(recipientEmail);
        // if the recipient user does not exist, return.
       Optional<User> optUser = Optional.ofNullable(recipientUser);
       if(!optUser.isPresent()){
    	   log.error("Cannot send email to {}, no user has such email", recipientEmail);
           return;
       }
       optUser.ifPresent(user -> {
    	   // adds some user info (first name, last name, title ...etc) to the context
           Locale locale = Locale.forLanguageTag(user.getLangKey());
           Context context = new Context(locale);
           // add contextVars to context
           contextMap.forEach((key, value) -> context.setVariable(key, value));
           addUserInfoToContext(context, user);
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
     * Helper method to add info of user to context
     * @param context
     * @param user
     * @return
     */
    private Context addUserInfoToContext(Context context, User user){
        context.setVariable(EmailConstants.FIRST_NAME, user.getFirstName());
        context.setVariable(EmailConstants.LAST_NAME, user.getLastName());
        context.setVariable(EmailConstants.EMAIL, user.getEmail());
        context.setVariable(EmailConstants.LOGIN, user.getLogin());
        context.setVariable(EmailConstants.TITLE, user.getTitle());
        context.setVariable(EmailConstants.TARGET_TITLE, user.getTargetTitle());
        return context;
    }
   
    @Async
    public void sendActivationEmail(String email, String activationUrl) {
    	log.debug("Sending activation e-mail to '{}'", email);
        Map<String, Object> contextMap = new HashMap<String, Object>();
        // adding vars to map -  those will be availabe to thymeleaf template
        contextMap.put(EmailConstants.ACTIVATION_URL, activationUrl);
        sendEmail(email, EmailConstants.SUBJECT_ACTIVATION, EmailConstants.TEMPALTE_ACTIVATION, contextMap);
    }

    // PLEASE REMOVE ME WHEN sendAnnualProcessStartedEmail(String email) is finished and hooked up in activiti process
    @Async
    public void sendAnnualProcessStartedEmail() {
        log.debug("THIS METHOD ProcessService.sendAnnualProcessStartedEmail NEEDS TO BE REMOVED");
        return;
    }
    
    @Async
    public void sendAnnualProcessStartedEmail(String email) {
        log.debug("Sending annual review process started e-mail to '{}'");
        Map<String, Object> contextMap = new HashMap<String, Object>();
        // adding vars to map -  those will be availabe to thymeleaf template
        // contextMap.put(EmailConstants.ACTIVATION_URL, activationUrl);
        sendEmail(email, EmailConstants.SUBJECT_ANNULA_REVIEW_STARTED, EmailConstants.TEMPALTE_ANNUAL_REVIEW_STARTED, contextMap);
    }

    @Async
    public void sendPeerReviewReminderEmail() {
        log.debug("Sending peer review process reminder e-mail to '{}'");
        Locale locale = Locale.forLanguageTag("en-us");
        Context context = new Context(locale);
        String content = templateEngine.process("peerReviewReminderEmail", context);

        sendEmail("prft.etm@gmail.com", "Test Subject", content, false, true);
    }
    
    @Async
    public void sendPeerReviewFeedbackRequestedEmail(String peerEmail,String peerFirstName, String reviewType, String reviewee) {
        log.debug("Sending peer review requested e-mail to '{}'");
        if(peerEmail== null || peerEmail.equals("")){
        	return;
        }
        Locale locale = Locale.forLanguageTag("en-us");
        Context context = new Context(locale);
        context.setVariable("peerFirstName", peerFirstName);
        context.setVariable("reviewType", reviewType);
        context.setVariable("reviewee", reviewee);
        String content = templateEngine.process("peerReviewFeedbackRequested", context);

        sendEmail(peerEmail, "ETM Feedback Requested", content, false, true);
    }
    
    @Async
    public void sendPeerReviewSubmittedEmail() {
        log.debug("Sending peer review submitted e-mail to '{}'");
        Locale locale = Locale.forLanguageTag("en-us");
        Context context = new Context(locale);
        String content = templateEngine.process("peerReviewFeedbackSubmitted", context);

        sendEmail("prft.etm@gmail.com", "Test Subject", content, false, true);
    }
    
    
    
}
