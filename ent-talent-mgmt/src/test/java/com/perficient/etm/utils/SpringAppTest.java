package com.perficient.etm.utils;

import javax.inject.Inject;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perficient.etm.Application;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test super class for configuring Spring test context.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
@Transactional
public class SpringAppTest {

	private static final Logger log = LoggerFactory.getLogger("SpringAppTest");
	
    @Inject
    protected ApplicationContext context;

    @Inject
    protected ObjectMapper objectMapper;

    @Test
    public void testContext() {
        assertThat(context).isNotNull();
        assertThat(objectMapper).isNotNull();
    }

	public Logger getLog() {
		return log;
	}
    
}
