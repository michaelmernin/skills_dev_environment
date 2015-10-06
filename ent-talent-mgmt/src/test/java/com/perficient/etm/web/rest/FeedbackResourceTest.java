package com.perficient.etm.web.rest;

import com.perficient.etm.domain.Feedback;
import com.perficient.etm.domain.Review;
import com.perficient.etm.repository.FeedbackRepository;
import com.perficient.etm.repository.RatingRepository;
import com.perficient.etm.utils.ResourceTestUtils;
import com.perficient.etm.utils.SpringAppTest;

import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.hasItem;

import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
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
 * Test class for the FeedbackResource REST controller.
 *
 * @see FeedbackResource
 */
public class FeedbackResourceTest extends SpringAppTest {

    private static final long REVIEW_ID = 1L;

    @Inject
    private FeedbackRepository feedbackRepository;
    
    @Inject
    private RatingRepository ratingRepository;

    private MockMvc restFeedbackMockMvc;

    private Feedback feedback;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        FeedbackResource feedbackResource = new FeedbackResource();
        ReflectionTestUtils.setField(feedbackResource, "feedbackRepository", feedbackRepository);
        ReflectionTestUtils.setField(feedbackResource, "ratingRepository", ratingRepository);
        this.restFeedbackMockMvc = MockMvcBuilders.standaloneSetup(feedbackResource).build();
    }

    @Before
    public void initTest() {
        feedback = new Feedback();
        Review review = new Review();
        review.setId(REVIEW_ID);
        feedback.setReview(review);
    }

    @Test
    @Transactional
    public void createFeedback() throws Exception {
        int count = (int) feedbackRepository.count();

        // Create the Feedback
        restFeedbackMockMvc.perform(post("/api/reviews/{reviewId}/feedback", REVIEW_ID)
                .contentType(ResourceTestUtils.APPLICATION_JSON_UTF8)
                .content(ResourceTestUtils.convertObjectToJsonBytes(feedback)))
                .andExpect(status().isCreated());

        // Validate the Feedback in the database
        List<Feedback> feedback = feedbackRepository.findAll();
        assertThat(feedback).hasSize(count + 1);
    }

    @Test
    @Transactional
    public void getAllFeedbacks() throws Exception {
        // Initialize the database
        feedbackRepository.saveAndFlush(feedback);

        // Get all the feedback
        restFeedbackMockMvc.perform(get("/api/reviews/{reviewId}/feedback", REVIEW_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].id").value(hasItem(feedback.getId().intValue())));
    }

    @Test
    @Transactional
    public void getFeedback() throws Exception {
        // Initialize the database
        feedbackRepository.saveAndFlush(feedback);

        // Get the feedback
        restFeedbackMockMvc.perform(get("/api/reviews/{reviewId}/feedback/{id}", REVIEW_ID, feedback.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(feedback.getId().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingFeedback() throws Exception {
        // Get the feedback
        restFeedbackMockMvc.perform(get("/api/reviews/{reviewId}/feedback/{id}", REVIEW_ID, Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFeedback() throws Exception {
        // Initialize the database
        feedbackRepository.saveAndFlush(feedback);

        int count = (int) feedbackRepository.count();

        // Update the feedback
        restFeedbackMockMvc.perform(put("/api/reviews/{reviewId}/feedback", REVIEW_ID)
                .contentType(ResourceTestUtils.APPLICATION_JSON_UTF8)
                .content(ResourceTestUtils.convertObjectToJsonBytes(feedback)))
                .andExpect(status().isOk());

        // Validate the Feedback in the database
        List<Feedback> feedback = feedbackRepository.findAll();
        assertThat(feedback).hasSize(count);
    }
}
