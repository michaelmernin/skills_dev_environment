package com.perficient.etm.web.rest;

import com.perficient.etm.Application;
import com.perficient.etm.domain.FeedbackStatus;
import com.perficient.etm.repository.FeedbackStatusRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the FeedbackStatusResource REST controller.
 *
 * @see FeedbackStatusResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class FeedbackStatusResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";

    @Inject
    private FeedbackStatusRepository feedbackStatusRepository;

    private MockMvc restFeedbackStatusMockMvc;

    private FeedbackStatus feedbackStatus;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        FeedbackStatusResource feedbackStatusResource = new FeedbackStatusResource();
        ReflectionTestUtils.setField(feedbackStatusResource, "feedbackStatusRepository", feedbackStatusRepository);
        this.restFeedbackStatusMockMvc = MockMvcBuilders.standaloneSetup(feedbackStatusResource).build();
    }

    @Before
    public void initTest() {
        feedbackStatus = new FeedbackStatus();
        feedbackStatus.setName(DEFAULT_NAME);
        feedbackStatus.setDescription(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createFeedbackStatus() throws Exception {
        // Validate the database is empty
        assertThat(feedbackStatusRepository.findAll()).hasSize(0);

        // Create the FeedbackStatus
        restFeedbackStatusMockMvc.perform(post("/api/feedbackStatuses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(feedbackStatus)))
                .andExpect(status().isOk());

        // Validate the FeedbackStatus in the database
        List<FeedbackStatus> feedbackStatuses = feedbackStatusRepository.findAll();
        assertThat(feedbackStatuses).hasSize(1);
        FeedbackStatus testFeedbackStatus = feedbackStatuses.iterator().next();
        assertThat(testFeedbackStatus.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFeedbackStatus.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllFeedbackStatuses() throws Exception {
        // Initialize the database
        feedbackStatusRepository.saveAndFlush(feedbackStatus);

        // Get all the feedbackStatuses
        restFeedbackStatusMockMvc.perform(get("/api/feedbackStatuses"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(feedbackStatus.getId().intValue()))
                .andExpect(jsonPath("$.[0].name").value(DEFAULT_NAME.toString()))
                .andExpect(jsonPath("$.[0].description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getFeedbackStatus() throws Exception {
        // Initialize the database
        feedbackStatusRepository.saveAndFlush(feedbackStatus);

        // Get the feedbackStatus
        restFeedbackStatusMockMvc.perform(get("/api/feedbackStatuses/{id}", feedbackStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(feedbackStatus.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingFeedbackStatus() throws Exception {
        // Get the feedbackStatus
        restFeedbackStatusMockMvc.perform(get("/api/feedbackStatuses/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFeedbackStatus() throws Exception {
        // Initialize the database
        feedbackStatusRepository.saveAndFlush(feedbackStatus);

        // Update the feedbackStatus
        feedbackStatus.setName(UPDATED_NAME);
        feedbackStatus.setDescription(UPDATED_DESCRIPTION);
        restFeedbackStatusMockMvc.perform(post("/api/feedbackStatuses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(feedbackStatus)))
                .andExpect(status().isOk());

        // Validate the FeedbackStatus in the database
        List<FeedbackStatus> feedbackStatuses = feedbackStatusRepository.findAll();
        assertThat(feedbackStatuses).hasSize(1);
        FeedbackStatus testFeedbackStatus = feedbackStatuses.iterator().next();
        assertThat(testFeedbackStatus.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFeedbackStatus.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void deleteFeedbackStatus() throws Exception {
        // Initialize the database
        feedbackStatusRepository.saveAndFlush(feedbackStatus);

        // Get the feedbackStatus
        restFeedbackStatusMockMvc.perform(delete("/api/feedbackStatuses/{id}", feedbackStatus.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<FeedbackStatus> feedbackStatuses = feedbackStatusRepository.findAll();
        assertThat(feedbackStatuses).hasSize(0);
    }
}
