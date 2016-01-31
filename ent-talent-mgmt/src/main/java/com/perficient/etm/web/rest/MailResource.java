package com.perficient.etm.web.rest;

import java.util.ArrayList;
import java.util.List;

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
    
    private final String MESSAGE_HEADER_FROM= "From";
    private final String MESSAGE_HEADER_TO= "To";
    private final String MESSAGE_HEADER_SUBJECT= "Subject";

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
    public  List<Message> getAllMessages() {
        log.debug("REST request to get all mail messages");
        MailMessage[] messages = smtpServer.getMessages();
        List<Message> returnMessages = new ArrayList<Message>();
        for(MailMessage message :messages){
            Message msg = new Message();
            msg.setBody(message.getBody());
            msg.setFrom(message.getFirstHeaderValue(MESSAGE_HEADER_FROM));
            msg.setTo(message.getFirstHeaderValue(MESSAGE_HEADER_TO));
            msg.setSubject(message.getFirstHeaderValue(MESSAGE_HEADER_SUBJECT));
            returnMessages.add(msg);
        }
        
        return returnMessages; 
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
        message.setText("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s");
        mailsender.send(message);
        return new ResponseEntity<SimpleMailMessage>(HttpStatus.CREATED);
    }
    
    
}

class Message{
    String to=null;
    String from=null;
    String subject=null;
    String body=null;
    public Message(){}
    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }
    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    
}