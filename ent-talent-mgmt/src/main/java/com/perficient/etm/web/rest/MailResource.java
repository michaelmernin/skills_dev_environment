package com.perficient.etm.web.rest;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.dumbster.smtp.MailMessage;
import com.dumbster.smtp.SmtpServer;
@RestController
@RequestMapping("/api")
public class MailResource {


    private final Logger log = LoggerFactory.getLogger(ReviewTypeResource.class);

    @Inject
    protected SmtpServer smtpServer;
    
    @Inject
    JavaMailSenderImpl mailsender;
    

    /**
     * GET  /mail/messages -> get all the mail messages.
     */
    @RequestMapping(value = "/mail/messages",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public MailMessage[] getAllMessages() {
        log.debug("REST request to get all mail messages");
        return smtpServer.getMessages();
    }
    
    /**
     * POST  /mail/test -> send a new test email.
     */
    @RequestMapping(value = "/mail/test",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SimpleMailMessage> SendTestMail() {
        log.debug("REST request to send test mail");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("test@sender.com");
        message.setTo("test@receiver.com");
        message.setSubject("test subject");
        message.setText("test message");
        mailsender.send(message);
        return new ResponseEntity<SimpleMailMessage>(HttpStatus.CREATED);
    }
    
    
}
