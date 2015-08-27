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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import java.util.List;
import java.util.Optional;

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
@ActiveProfiles("dev")
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
        int count = (int) feedbackStatusRepository.count();

        // Create the FeedbackStatus
        restFeedbackStatusMockMvc.perform(post("/api/feedbackStatuses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(feedbackStatus)))
                .andExpect(status().isOk());

        // Validate the FeedbackStatus in the database
        List<FeedbackStatus> feedbackStatuses = feedbackStatusRepository.findAll();
        assertThat(feedbackStatuses).hasSize(count + 1);
        Optional<FeedbackStatus> optional = feedbackStatuses.stream().filter(fs -> {return DEFAULT_NAME.equals(fs.getName());}).findAny();
        assertThat(optional.isPresent()).isTrue();
        FeedbackStatus testFeedbackStatus = optional.get();
        assertThat(testFeedbackStatus.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFeedbackStatus.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllFeedbackStatuses() throws Exception {
        // Read a feedback status
        FeedbackStatus feedbackStatus = feedbackStatusRepository.findOne(1L);

        // Get all the feedbackStatuses
        restFeedbackStatusMockMvc.perform(get("/api/feedbackStatuses"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(feedbackStatus.getId().intValue()))
                .andExpect(jsonPath("$.[0].name").value(feedbackStatus.getName()))
                .andExpect(jsonPath("$.[0].description").value(feedbackStatus.getDescription()));
    }

    @Test
    @Transactional
    public void getFeedbackStatus() throws Exception {
        // Read a feedback status
        FeedbackStatus feedbackStatus = feedbackStatusRepository.findOne(1L);

        // Get the feedbackStatus
        restFeedbackStatusMockMvc.perform(get("/api/feedbackStatuses/{id}", feedbackStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(feedbackStatus.getId().intValue()))
            .andExpect(jsonPath("$.name").value(feedbackStatus.getName()))
            .andExpect(jsonPath("$.description").value(feedbackStatus.getDescription()));
    }

    @Test
    @Transactional
    public void getNonExistingFeedbackStatus() throws Exception {
        // Get the feedbackStatus
        restFeedbackStatusMockMvc.perform(get("/api/feedbackStatuses/{id}", 404L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFeedbackStatus() throws Exception {
        int count = (int) feedbackStatusRepository.count();
        
        // Read a feedback status
        FeedbackStatus feedbackStatus = feedbackStatusRepository.findOne(1L);

        // Update the feedbackStatus
        feedbackStatus.setName(UPDATED_NAME);
        feedbackStatus.setDescription(UPDATED_DESCRIPTION);
        restFeedbackStatusMockMvc.perform(post("/api/feedbackStatuses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(feedbackStatus)))
                .andExpect(status().isOk());

        // Validate the FeedbackStatus in the database
        List<FeedbackStatus> feedbackStatuses = feedbackStatusRepository.findAll();
        assertThat(feedbackStatuses).hasSize(count);
        Optional<FeedbackStatus> optional = feedbackStatuses.stream().filter(fs -> {return fs.getId() == 1L;}).findAny();
        assertThat(optional.isPresent()).isTrue();
        FeedbackStatus testFeedbackStatus = optional.get();
        assertThat(testFeedbackStatus.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFeedbackStatus.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void deleteFeedbackStatus() throws Exception {
        int count = (int) feedbackStatusRepository.count();
        
        // Read a feedback status
        FeedbackStatus feedbackStatus = feedbackStatusRepository.findOne(1L);

        // Get the feedbackStatus
        restFeedbackStatusMockMvc.perform(delete("/api/feedbackStatuses/{id}", feedbackStatus.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<FeedbackStatus> feedbackStatuses = feedbackStatusRepository.findAll();
        assertThat(feedbackStatuses).hasSize(count - 1);
    }
}
