package com.perficient.etm.service;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.perficient.etm.utils.SpringAppTest;

public class MailServiceTest extends SpringAppTest {
    
    private final String MESSAGE_HEADER_FROM= "From";
    private final String MESSAGE_HEADER_TO= "To";
    private final String MESSAGE_HEADER_SUBJECT= "Subject";
    
    @Inject
    JavaMailSenderImpl mailsender;
    
    @Test
    public void testEmail() throws InterruptedException, MessagingException {
        //assertTrue(smtpServer.isReady());
        int count = smtpServer.getReceivedMessages().length;
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("test@receiver.com");
        message.setSubject("test subject");
        message.setText("test message");
        mailsender.send(message);
        
        MimeMessage[] messages = smtpServer.getReceivedMessages();
        assertEquals(count + 1, messages.length);
        assertEquals("test subject", messages[count].getSubject());
        assertEquals("test@receiver.com", StringUtils.join(messages[count].getAllRecipients(),""));
        try {
			assertEquals("test message", messages[count].getContent().toString().trim());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
