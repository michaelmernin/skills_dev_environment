package com.perficient.etm.web.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.icegreen.greenmail.util.GreenMail;
import com.perficient.etm.service.MailService;
import com.perficient.etm.service.UserService;

// TODO - remove this resource from production, only available in lower environments
@RestController
@RequestMapping("/api")
public class MailResource {

    private final Logger log = LoggerFactory.getLogger(ReviewTypeResource.class);
    List<Message> messages = new ArrayList<Message>();
    Set<Integer> hashset = new HashSet<Integer>();

    @Autowired(required = false)
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
    public List<Message> getAllMessages() {
        log.debug("REST request to get all mail messages");
        updateEmails();
        updateHashes();
        return userSvc.getUserFromLogin().map(usr ->{
            return messages
                    .stream()
                    .filter(msg -> msg.getTo().equals(usr.getEmail()))
                    .collect(Collectors.toList());
        }).orElse(null);
    }

    private void updateHashes() {
        messages.forEach(msg -> hashset.add(msg.getHashcode()));
    }
    private void updateEmails(){
        Optional.ofNullable(smtpServer).map(GreenMail::getReceivedMessages).ifPresent(msgs -> {
            Arrays.stream(msgs)
            .filter(msg -> !hashset.contains(msg.hashCode()))
            .forEach(newMsg -> messages.add(new Message(newMsg)));
        });
    }

    /**
     * GET /mail/messages -> get mail message by hash.
     */
    @RequestMapping(value = "/mail/messageHtml/{hash}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getMessageByhashCode(@PathVariable int hash) {
        log.debug("REST request to get message with hashcode {}", hash);
        return messages
                .stream()
                .filter(msg -> msg.getHashcode() == hash)
                .findFirst()
                .map(msg ->{ 
                    msg.setOpen(true);
                    return msg.getBody();
                 })
                .map(body -> {return body;})
                .orElse("<p>NOT FOUND</p>");
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

}

class Message {
    String to = null;
    String from = null;
    String subject = null;
    String body = null;
    String id = null;
    int hashcode;
    boolean open = false;

    public Message() {
    }

    public Message(SimpleMailMessage message) {
        this.to = StringUtils.join(message.getTo(), ",");
        this.from = message.getFrom();
        this.subject = message.getSubject();
        this.body = message.getText();
        this.hashcode = message.hashCode();
        this.open = false;
    }

    public Message(MimeMessage mimeMessage) {
        Optional.ofNullable(mimeMessage).ifPresent(mime -> {
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
                this.open = false;
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

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

}