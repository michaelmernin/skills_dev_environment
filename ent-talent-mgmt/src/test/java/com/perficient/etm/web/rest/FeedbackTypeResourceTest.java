package com.perficient.etm.web.rest;

import com.perficient.etm.Application;
import com.perficient.etm.domain.FeedbackType;
import com.perficient.etm.repository.FeedbackTypeRepository;

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
 * Test class for the FeedbackTypeResource REST controller.
 *
 * @see FeedbackTypeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class FeedbackTypeResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";

    @Inject
    private FeedbackTypeRepository feedbackTypeRepository;

    private MockMvc restFeedbackTypeMockMvc;

    private FeedbackType feedbackType;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        FeedbackTypeResource feedbackTypeResource = new FeedbackTypeResource();
        ReflectionTestUtils.setField(feedbackTypeResource, "feedbackTypeRepository", feedbackTypeRepository);
        this.restFeedbackTypeMockMvc = MockMvcBuilders.standaloneSetup(feedbackTypeResource).build();
    }

    @Before
    public void initTest() {
        feedbackType = new FeedbackType();
        feedbackType.setName(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createFeedbackType() throws Exception {
        // Validate the database is empty
        assertThat(feedbackTypeRepository.findAll()).hasSize(0);

        // Create the FeedbackType
        restFeedbackTypeMockMvc.perform(post("/api/feedbackTypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(feedbackType)))
                .andExpect(status().isOk());

        // Validate the FeedbackType in the database
        List<FeedbackType> feedbackTypes = feedbackTypeRepository.findAll();
        assertThat(feedbackTypes).hasSize(1);
        FeedbackType testFeedbackType = feedbackTypes.iterator().next();
        assertThat(testFeedbackType.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void getAllFeedbackTypes() throws Exception {
        // Initialize the database
        feedbackTypeRepository.saveAndFlush(feedbackType);

        // Get all the feedbackTypes
        restFeedbackTypeMockMvc.perform(get("/api/feedbackTypes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(feedbackType.getId().intValue()))
                .andExpect(jsonPath("$.[0].name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getFeedbackType() throws Exception {
        // Initialize the database
        feedbackTypeRepository.saveAndFlush(feedbackType);

        // Get the feedbackType
        restFeedbackTypeMockMvc.perform(get("/api/feedbackTypes/{id}", feedbackType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(feedbackType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingFeedbackType() throws Exception {
        // Get the feedbackType
        restFeedbackTypeMockMvc.perform(get("/api/feedbackTypes/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFeedbackType() throws Exception {
        // Initialize the database
        feedbackTypeRepository.saveAndFlush(feedbackType);

        // Update the feedbackType
        feedbackType.setName(UPDATED_NAME);
        restFeedbackTypeMockMvc.perform(post("/api/feedbackTypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(feedbackType)))
                .andExpect(status().isOk());

        // Validate the FeedbackType in the database
        List<FeedbackType> feedbackTypes = feedbackTypeRepository.findAll();
        assertThat(feedbackTypes).hasSize(1);
        FeedbackType testFeedbackType = feedbackTypes.iterator().next();
        assertThat(testFeedbackType.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void deleteFeedbackType() throws Exception {
        // Initialize the database
        feedbackTypeRepository.saveAndFlush(feedbackType);

        // Get the feedbackType
        restFeedbackTypeMockMvc.perform(delete("/api/feedbackTypes/{id}", feedbackType.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<FeedbackType> feedbackTypes = feedbackTypeRepository.findAll();
        assertThat(feedbackTypes).hasSize(0);
    }
}