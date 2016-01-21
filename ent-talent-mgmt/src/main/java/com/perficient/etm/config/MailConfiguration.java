package com.perficient.etm.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.dumbster.smtp.SmtpServer;
import com.dumbster.smtp.mailstores.RollingMailStore;

import static java.lang.System.out;

import java.util.Properties;
import java.util.concurrent.Executors;

@Configuration
public class MailConfiguration {

    private static final String ENV_SPRING_MAIL = "spring.mail.";
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final String PROP_HOST = ENV_SPRING_MAIL+"host";
    private static final String DEFAULT_PROP_HOST = "localhost";
    private static final String PROP_PORT = ENV_SPRING_MAIL+"port";
    private static final String DEAFAULT_PROP_PORT = "0";
    private static final String PROP_USER = ENV_SPRING_MAIL+"user";
    private static final String PROP_PASSWORD = ENV_SPRING_MAIL+"password";
    private static final String PROP_PROTO = ENV_SPRING_MAIL+"protocol";
    private static final String PROP_TLS = ENV_SPRING_MAIL+"tls";
    private static final String DEAFAULT_PROP_TLS = "false";
    private static final String PROP_AUTH = ENV_SPRING_MAIL+"auth";
    private static final String DEFAULT_PROP_AUTH = "false";
    
    private static final String PROP_SMTP_AUTH = "mail.smtp.auth";
    private static final String PROP_STARTTLS = "mail.smtp.starttls.enable";
    private static final String PROP_TRANSPORT_PROTO = "mail.transport.protocol";
    
    @Value("${"+PROP_HOST+":"+DEFAULT_PROP_HOST+"}")
    private String host;
    
    @Value("${"+PROP_PORT+":"+DEAFAULT_PROP_PORT+"}")
    private int port;
    
    @Value("${"+PROP_USER+"}")
    private String user;
    
    @Value("${"+PROP_PASSWORD+"}")
    private String password;
    
    @Value("${"+PROP_PROTO+"}")
    private String protocol;
    
    @Value("${"+PROP_TLS+":"+DEAFAULT_PROP_TLS+"}")
    private Boolean tls;
    
    @Value("${"+PROP_AUTH+":"+DEFAULT_PROP_AUTH+"}")
    private Boolean auth;

    private final Logger log = LoggerFactory.getLogger(MailConfiguration.class);

    @Bean
    public JavaMailSenderImpl javaMailSender() {
        log.debug("Configuring mail server");
        
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        if (host != null && !host.isEmpty()) {
            sender.setHost(host);
        } else {
            log.warn("Warning! Your SMTP server is not configured. We will try to use one on localhost.");
            log.debug("Did you configure your SMTP settings in your application.yml?");
            sender.setHost(DEFAULT_HOST);
        }
        sender.setPort(port);
        sender.setUsername(user);
        sender.setPassword(password);

        Properties sendProperties = new Properties();
        sendProperties.setProperty(PROP_SMTP_AUTH, auth.toString());
        sendProperties.setProperty(PROP_STARTTLS, tls.toString());
        sendProperties.setProperty(PROP_TRANSPORT_PROTO, protocol);
        sender.setJavaMailProperties(sendProperties);
        return sender;
    }
    
    @Bean
    @Profile({Constants.SPRING_PROFILE_DEVELOPMENT,Constants.SPRING_PROFILE_TEST,Constants.SPRING_PROFILE_UAT})
    public SmtpServer initDumpsterSmtpServer(){
        SmtpServer server = new SmtpServer();
        server.setPort(port);
        server.setMailStore(new RollingMailStore());
        Executors.newSingleThreadExecutor().execute(() -> {
            server.run();
        });
        return server;
    }
}
