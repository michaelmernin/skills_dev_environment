package com.perficient.etm.utils;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTestPropertiesListener;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
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
@TestExecutionListeners({
    IntegrationTestPropertiesListener.class,
    DependencyInjectionTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    SqlScriptsTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    AuthenticationTestExecutionListener.class
})
@Transactional
public class SpringAppTest {

    @Inject
    protected ApplicationContext context;

    @Inject
    protected ObjectMapper objectMapper;

    @Test
    public void testContext() {
        assertThat(context).isNotNull();
        assertThat(objectMapper).isNotNull();
    }
}
