package com.perficient.etm.utils;

import javax.inject.Inject;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.IntegrationTestPropertiesListener;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.dumbster.smtp.SmtpServer;
import com.dumbster.smtp.mailstores.RollingMailStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perficient.etm.Application;
import com.perficient.etm.service.ReviewService;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import static java.lang.System.out;

/**
 * Test super class for configuring Spring test context.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
@Transactional
public class SpringAppTest {
	
	static SmtpServer server;
	
	@BeforeClass
	public static void initSmtp(){
		server = new SmtpServer();
		server.setPort(1025);
		server.setMailStore(new RollingMailStore());
		Executors.newSingleThreadExecutor().execute(()->{
			server.run();
			boolean ready = server.isReady();
			out.println(ready);
		});
		
	}
	
	@AfterClass
	public static void stopSmtp() throws InterruptedException, ExecutionException{
		if (server != null && !server.isStopped())
			server.stop();
	}
	
	private static final Logger log = LoggerFactory.getLogger("SpringAppTest");
	
    @Inject
    protected ApplicationContext context;

    @Inject
    protected ObjectMapper objectMapper;

    @Test
    public void testContext() {
        assertThat(context).isNotNull();
        assertThat(objectMapper).isNotNull();
        assertThat(context.getBean(ReviewService.class)).isNotNull();
    }

	public Logger getLog() {
		return log;
	}

	public static SmtpServer getServer() {
		return server;
	}

	public static void setServer(SmtpServer server) {
		SpringAppTest.server = server;
	}
	
}
