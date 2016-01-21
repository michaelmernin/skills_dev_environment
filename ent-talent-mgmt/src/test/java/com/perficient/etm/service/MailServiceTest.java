package com.perficient.etm.service;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;
import javax.mail.MessagingException;

import org.junit.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.dumbster.smtp.MailMessage;
import com.dumbster.smtp.SmtpServer;
import com.perficient.etm.utils.SpringAppTest;

public class MailServiceTest extends SpringAppTest {
    
    private final String MESSAGE_HEADER_FROM= "From";
    private final String MESSAGE_HEADER_TO= "To";
    private final String MESSAGE_HEADER_SUBJECT= "Subject";
    
    @Inject
    JavaMailSenderImpl mailsender;
    
    @Test
    public void testEmail() throws InterruptedException, MessagingException {
        
        SimpleMailMessage message = new SimpleMailMessage();
 
        message.setFrom("test@sender.com");
        message.setTo("test@receiver.com");
        message.setSubject("test subject");
        message.setText("test message");
        mailsender.send(message);
        
        MailMessage[] messages = smtpServer.getMessages();        
        assertEquals(1, messages.length);
        assertEquals("test subject", messages[0].getFirstHeaderValue(MESSAGE_HEADER_SUBJECT));
        assertEquals("test@sender.com", messages[0].getFirstHeaderValue(MESSAGE_HEADER_FROM));
        assertEquals("test@receiver.com", messages[0].getFirstHeaderValue(MESSAGE_HEADER_TO));
        assertEquals("test message", messages[0].getBody());
        
    }
    
    
    

}
