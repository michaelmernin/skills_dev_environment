package com.perficient.etm.web.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.icegreen.greenmail.util.GreenMail;
import com.perficient.etm.repository.UserRepository;
import com.perficient.etm.service.MailService;
import com.perficient.etm.service.UserService;
// TODO - remove this resource from production, only available in lower environments
@RestController
@RequestMapping("/api")
public class MailResource {

    private final String MESSAGE_HEADER_FROM = "From";
    private final String MESSAGE_HEADER_TO = "To";
    private final String MESSAGE_HEADER_SUBJECT = "Subject";

    private final Logger log = LoggerFactory.getLogger(ReviewTypeResource.class);
    List<Message> messages = new ArrayList<Message>();

    @Autowired(required=false)
    protected GreenMail smtpServer;

    @Inject
    JavaMailSenderImpl mailsender;
    
    @Inject
    MailService mailService;
    
    @Inject
    private UserService userSvc;

    /**
     * GET /mail/messages -> get all the mail messages.
     */
    @RequestMapping(value = "/mail/messages", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Message>  getAllMessages() {
        log.debug("REST request to get all mail messages");
        List<Message> filteredMessages = new ArrayList<Message>();
        Optional.ofNullable(smtpServer).map(GreenMail::getReceivedMessages).ifPresent(msgs -> {
        	messages.clear();
        	userSvc.getUserFromLogin().ifPresent(user -> {
        		for (MimeMessage message : msgs) {
            		Message m = new Message(message);
                	messages.add(m);
                	if(m.getTo().equals(user.getEmail())){
                		filteredMessages.add(m);
                	}
                }
            });
        });
        return filteredMessages;
    }
    /**
     * GET /mail/messages -> get all the mail messages.
     */
    @RequestMapping(value = "/mail/messageHtml/{hash}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getMessageByhashCode(@PathVariable int hash) {
        log.debug("REST request to get message with hashcode {}",hash);
		for (Message message : messages) {
            if(message.getHashcode() == hash){
            	return message.getBody();
            }
        }
       return "<p>NOT FOUND</p>";
    }
    

    /**
     * POST /mail/test -> send a new test email.
     */
    @RequestMapping(value = "/mail/test", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SimpleMailMessage> SendTestMail() {
        log.debug("REST request to send test mail");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("donotreply@perficient.com");
        message.setTo("ahmed.musallam@perficient.com");
        message.setSubject("test subject");
        message.setText(
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s");
        mailsender.send(message);
        messages.add(new Message(message));
        return new ResponseEntity<SimpleMailMessage>(HttpStatus.CREATED);
    }

    /**
     * DELETE /mail/clear -> delete all existing mail
     */
    @RequestMapping(value = "/mail/clear", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SimpleMailMessage> clear() {
        log.debug("REST request to delete all mail");
        smtpServer.reset();
        messages.clear();
        return new ResponseEntity<SimpleMailMessage>(HttpStatus.OK);
    }

    /**
     * POST /mail/send -> send a new email.
     */
    @RequestMapping(value = "/mail/send", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Message> SendMail(@RequestBody Message msg) {
        log.debug("REST request to send new mail");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(msg.getFrom());
        message.setTo(msg.getTo());
        message.setSubject(msg.getSubject());
        message.setText(msg.getBody());
        messages.add(new Message(message));
        mailsender.send(message);
        return new ResponseEntity<Message>(HttpStatus.OK);
    }
    
}


class Message{
    String to=null;
    String from=null;
    String subject=null;
    String body=null;
    String id=null;
    int hashcode;
    public Message(){}
    public Message(SimpleMailMessage message){
    	this.to= StringUtils.join(message.getTo(),",");
    	this.from = message.getFrom();
    	this.subject = message.getSubject();
    	this.body = message.getText();
    	this.hashcode = message.hashCode();
    }
    public Message(MimeMessage mimeMessage){
		Optional.ofNullable(mimeMessage).ifPresent(mime ->{
			try {
				Optional.ofNullable(mime.getAllRecipients()).ifPresent(recipients -> {
					this.to = StringUtils.join(recipients, ",");
				});
				Optional.ofNullable(mime.getSender()).ifPresent(sender -> {
					this.from = sender.toString();
				});
				Optional.ofNullable(mime.getSubject()).ifPresent(subject -> {
					this.subject = subject;
				});
				Optional.ofNullable(mime.getContent()).ifPresent(content -> {
					this.body = content.toString();
				});
				this.hashcode = mimeMessage.hashCode();
			} catch (MessagingException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
		});
    }
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
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public int getHashcode() {
        return hashcode;
    }
    public void setHashcode(int hashcode) {
        this.hashcode = hashcode;
    }
    
}