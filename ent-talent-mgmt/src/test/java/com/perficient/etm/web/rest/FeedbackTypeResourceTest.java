package com.perficient.etm.web.rest;

import com.perficient.etm.domain.FeedbackType;
import com.perficient.etm.repository.FeedbackTypeRepository;
import com.perficient.etm.utils.ResourceTestUtils;
import com.perficient.etm.utils.SpringAppTest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
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
 * Test class for the FeedbackTypeResource REST controller.
 *
 * @see FeedbackTypeResource
 */
public class FeedbackTypeResourceTest extends SpringAppTest {

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
        int count = (int) feedbackTypeRepository.count();

        // Create the FeedbackType
        restFeedbackTypeMockMvc.perform(post("/api/feedbackTypes")
                .contentType(ResourceTestUtils.APPLICATION_JSON_UTF8)
                .content(ResourceTestUtils.convertObjectToJsonBytes(feedbackType)))
                .andExpect(status().isOk());

        // Validate the FeedbackType in the database
        List<FeedbackType> feedbackTypes = feedbackTypeRepository.findAll();
        assertThat(feedbackTypes).hasSize(count + 1);
        Optional<FeedbackType> optional = feedbackTypes.stream().filter(ft -> {return DEFAULT_NAME.equals(ft.getName());}).findAny();
        assertThat(optional.isPresent()).isTrue();
        FeedbackType testFeedbackType = optional.get();
        assertThat(testFeedbackType.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void getAllFeedbackTypes() throws Exception {
        // Read a feedback type
        FeedbackType feedbackType = feedbackTypeRepository.findOne(1L);

        // Get all the feedbackTypes
        restFeedbackTypeMockMvc.perform(get("/api/feedbackTypes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(feedbackType.getId().intValue()))
                .andExpect(jsonPath("$.[0].name").value(feedbackType.getName()));
    }

    @Test
    @Transactional
    public void getFeedbackType() throws Exception {
        // Read a feedback type
        FeedbackType feedbackType = feedbackTypeRepository.findOne(1L);

        // Get the feedbackType
        restFeedbackTypeMockMvc.perform(get("/api/feedbackTypes/{id}", feedbackType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(feedbackType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(feedbackType.getName()));
    }

    @Test
    @Transactional
    public void getNonExistingFeedbackType() throws Exception {
        // Get the feedbackType
        restFeedbackTypeMockMvc.perform(get("/api/feedbackTypes/{id}", 404L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFeedbackType() throws Exception {
        int count = (int) feedbackTypeRepository.count();
        
        // Read a feedback type
        FeedbackType feedbackType = feedbackTypeRepository.findOne(1L);

        // Update the feedbackType
        feedbackType.setName(UPDATED_NAME);
        restFeedbackTypeMockMvc.perform(post("/api/feedbackTypes")
                .contentType(ResourceTestUtils.APPLICATION_JSON_UTF8)
                .content(ResourceTestUtils.convertObjectToJsonBytes(feedbackType)))
                .andExpect(status().isOk());

        // Validate the FeedbackType in the database
        List<FeedbackType> feedbackTypes = feedbackTypeRepository.findAll();
        assertThat(feedbackTypes).hasSize(count);
        Optional<FeedbackType> optional = feedbackTypes.stream().filter(ft -> {return ft.getId() == 1L;}).findAny();
        assertThat(optional.isPresent()).isTrue();
        FeedbackType testFeedbackType = optional.get();
        assertThat(testFeedbackType.getName()).isEqualTo(UPDATED_NAME);
    }
}
