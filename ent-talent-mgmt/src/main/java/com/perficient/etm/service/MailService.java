package com.perficient.etm.service;

import com.perficient.etm.domain.User;
import org.apache.commons.lang.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.mail.internet.MimeMessage;
import java.util.Locale;

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

    /**
     * System default email address that sends the e-mails.
     */
    private String from;

    @PostConstruct
    public void init() {
        this.from = env.getProperty("spring.mail.from");
    }

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
            log.debug("Sent e-mail to User '{}'", to);
        } catch (Exception e) {
            log.warn("E-mail could not be sent to user '{}', exception is: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendActivationEmail(User user, String baseUrl) {
        log.debug("Sending activation e-mail to '{}'", user.getEmail());
        Locale locale = Locale.forLanguageTag(user.getLangKey());
        Context context = new Context(locale);
        context.setVariable("user", user);
        context.setVariable("baseUrl", baseUrl);
        String content = templateEngine.process("activationEmail", context);
        String subject = messageSource.getMessage("email.activation.title", null, locale);
        sendEmail(user.getEmail(), subject, content, false, true);
    }

    @Async
    public void sendAnnualProcessStartedEmail() {
        log.debug("Sending annual review process started e-mail to '{}'");
        Locale locale = Locale.forLanguageTag("en-us");
        Context context = new Context(locale);
        String content = templateEngine.process("reviewStartedEmail", context);

        sendEmail("Test@test.org", "Test Subject", content, false, true);
    }

    @Async
    public void sendPeerReviewReminderEmail() {
        log.debug("Sending peer review process reminder e-mail to '{}'");
        Locale locale = Locale.forLanguageTag("en-us");
        Context context = new Context(locale);
        String content = templateEngine.process("peerReviewReminderEmail", context);

        sendEmail("Test@test.org", "Test Subject", content, false, true);
    }
    
    @Async
    public void sendPeerReviewFeedbackRequestedEmail() {
        log.debug("Sending peer review requested e-mail to '{}'");
        Locale locale = Locale.forLanguageTag("en-us");
        Context context = new Context(locale);
        String content = templateEngine.process("peerReviewFeedbackRequested", context);

        sendEmail("Test@test.org", "Test Subject", content, false, true);
    }
    
    @Async
    public void sendPeerReviewSubmittedEmail() {
        log.debug("Sending peer review submitted e-mail to '{}'");
        Locale locale = Locale.forLanguageTag("en-us");
        Context context = new Context(locale);
        String content = templateEngine.process("peerReviewFeedbackSubmitted", context);

        sendEmail("Test@test.org", "Test Subject", content, false, true);
    }
}
