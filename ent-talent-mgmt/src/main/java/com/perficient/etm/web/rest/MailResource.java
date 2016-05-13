package com.perficient.etm.web.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;

import org.apache.commons.io.IOUtils;
import org.apache.geronimo.mail.util.QuotedPrintable;
import org.apache.geronimo.mail.util.QuotedPrintableEncoder;
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
import com.dumbster.smtp.MailMessage;
import com.dumbster.smtp.SmtpServer;
import com.perficient.etm.repository.UserRepository;
import com.perficient.etm.service.MailService;
// TODO - remove this resource from production, only available in lower environments
@RestController
@RequestMapping("/api")
public class MailResource {

    private final String MESSAGE_HEADER_FROM = "From";
    private final String MESSAGE_HEADER_TO = "To";
    private final String MESSAGE_HEADER_SUBJECT = "Subject";

    private final Logger log = LoggerFactory.getLogger(ReviewTypeResource.class);
    private List<Integer> messageList = new ArrayList<Integer>();

    @Autowired(required=false)
    protected SmtpServer smtpServer;

    @Inject
    JavaMailSenderImpl mailsender;
    
    @Inject
    MailService mailService;
    
    @Inject
    private UserRepository userRepository;

    /**
     * GET /mail/messages -> get all the mail messages.
     */
    @RequestMapping(value = "/mail/messages", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Message> getAllMessages() {
        log.debug("REST request to get all mail messages");
        MailMessage[] messages = smtpServer.getMessages();
        List<Message> returnMessages = new ArrayList<Message>();
        for (MailMessage message : messages) {
            Message msg = new Message();
            msg.setBody(decodeQP(message.getBody()));
            msg.setFrom(message.getFirstHeaderValue(MESSAGE_HEADER_FROM));
            msg.setTo(message.getFirstHeaderValue(MESSAGE_HEADER_TO));
            msg.setSubject(message.getFirstHeaderValue(MESSAGE_HEADER_SUBJECT));
            msg.setHashcode(message.hashCode());
            returnMessages.add(msg);
        }

        return returnMessages;
    }
    /**
     * GET /mail/messages -> get all the mail messages.
     */
    @RequestMapping(value = "/mail/messageHtml/{hash}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getMessageByhashCode(@PathVariable int hash) {
        log.debug("REST request to get message with hashcode {}",hash);
        MailMessage[] messages = smtpServer.getMessages();
        for (MailMessage message : messages) {
            int hashcode = message.hashCode();
            if(message.hashCode() == hash){
            	return decodeQP(message.getBody());
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
        messageList.add(message.hashCode());
        return new ResponseEntity<SimpleMailMessage>(HttpStatus.CREATED);
    }

    /**
     * DELETE /mail/clear -> delete all existing mail
     */
    @RequestMapping(value = "/mail/clear", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SimpleMailMessage> clear() {
        log.debug("REST request to delete all mail");
        smtpServer.clearMessages();
        messageList.clear();
        return new ResponseEntity<SimpleMailMessage>(HttpStatus.OK);
    }

    /**
     * POST /mail/save -> send a new email.
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
        messageList.add(message.hashCode());
        mailsender.send(message);
        return new ResponseEntity<Message>(HttpStatus.OK);
    }
    
    private String decodeQP(String qpString){
    	String out="";
    	InputStream inputStream = null;
    	try {
    		inputStream  = IOUtils.toInputStream(qpString);
    		inputStream = MimeUtility.decode(inputStream, "quoted-printable");
			out = IOUtils.toString(inputStream);
		} catch (MessagingException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			Optional.ofNullable(inputStream).ifPresent(is -> {
					try {
						is.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			);
		}
    	
        return out;
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