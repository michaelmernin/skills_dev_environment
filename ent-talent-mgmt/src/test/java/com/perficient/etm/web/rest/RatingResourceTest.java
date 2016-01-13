package com.perficient.etm.web.rest;

import com.perficient.etm.domain.Rating;
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
 * Test class for the RatingResource REST controller.
 *
 * @see RatingResource
 */
public class RatingResourceTest extends SpringAppTest {

    private static final long FEEDBACK_ID = 1L;
    private static final long REVIEW_ID = 2L;
    private static final Double DEFAULT_SCORE = 0.0;
    private static final Double UPDATED_SCORE = 3.25;
    private static final String DEFAULT_COMMENT = "SAMPLE_TEXT";
    private static final String UPDATED_COMMENT = "UPDATED_TEXT";

    private static final Boolean DEFAULT_VISIBLE = false;
    private static final Boolean UPDATED_VISIBLE = true;

    @Inject
    private RatingRepository ratingRepository;

    private MockMvc restRatingMockMvc;

    private Rating rating;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RatingResource ratingResource = new RatingResource();
        ReflectionTestUtils.setField(ratingResource, "ratingRepository", ratingRepository);
        this.restRatingMockMvc = MockMvcBuilders.standaloneSetup(ratingResource).build();
    }

    @Before
    public void initTest() {
        rating = new Rating();
        rating.setScore(DEFAULT_SCORE);
        rating.setComment(DEFAULT_COMMENT);
        rating.setVisible(DEFAULT_VISIBLE);
    }

    @Test
    @Transactional
    public void createRating() throws Exception {
        int count = (int) ratingRepository.count();

        // Create the Rating
        restRatingMockMvc.perform(post("/api/reviews/{reviewId}/feedback/{feedbackId}/ratings", REVIEW_ID, FEEDBACK_ID)
                .contentType(ResourceTestUtils.APPLICATION_JSON_UTF8)
                .content(ResourceTestUtils.convertObjectToJsonBytes(rating)))
                .andExpect(status().isCreated());

        // Validate the Rating in the database
        List<Rating> ratings = ratingRepository.findAll();
        assertThat(ratings).hasSize(count + 1);
        Rating testRating = ratings.get(ratings.size() - 1);
        assertThat(testRating.getScore()).isEqualTo(DEFAULT_SCORE);
        assertThat(testRating.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testRating.isVisible()).isEqualTo(DEFAULT_VISIBLE);
    }

    @Test
    @Transactional
    public void getAllRatings() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get all the ratings
        restRatingMockMvc.perform(get("/api/reviews/{reviewId}/feedback/{feedbackId}/ratings", REVIEW_ID, FEEDBACK_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(rating.getId().intValue())))
                .andExpect(jsonPath("$.[*].score").value(hasItem(DEFAULT_SCORE.doubleValue())))
                .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
                .andExpect(jsonPath("$.[*].visible").value(hasItem(DEFAULT_VISIBLE.booleanValue())));
    }

    @Test
    @Transactional
    public void getRating() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        // Get the rating
        restRatingMockMvc.perform(get("/api/reviews/{reviewId}/feedback/{feedbackId}/ratings/{id}", REVIEW_ID, FEEDBACK_ID, rating.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(rating.getId().intValue()))
            .andExpect(jsonPath("$.score").value(DEFAULT_SCORE.doubleValue()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.visible").value(DEFAULT_VISIBLE.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingRating() throws Exception {
        // Get the rating
        restRatingMockMvc.perform(get("/api/reviews/{reviewId}/feedback/{feedbackId}/ratings/{id}", REVIEW_ID, FEEDBACK_ID, Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRating() throws Exception {
        // Initialize the database
        ratingRepository.saveAndFlush(rating);

        int count = (int) ratingRepository.count();

        // Update the rating
        rating.setScore(UPDATED_SCORE);
        rating.setComment(UPDATED_COMMENT);
        rating.setVisible(UPDATED_VISIBLE);
        restRatingMockMvc.perform(put("/api/reviews/{reviewId}/feedback/{feedbackId}/ratings", REVIEW_ID, FEEDBACK_ID)
                .contentType(ResourceTestUtils.APPLICATION_JSON_UTF8)
                .content(ResourceTestUtils.convertObjectToJsonBytes(rating)))
                .andExpect(status().isOk());

        // Validate the Rating in the database
        List<Rating> ratings = ratingRepository.findAll();
        assertThat(ratings).hasSize(count);
        Rating testRating = ratings.get(ratings.size() - 1);
        assertThat(testRating.getScore()).isEqualTo(UPDATED_SCORE);
        assertThat(testRating.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testRating.isVisible()).isEqualTo(UPDATED_VISIBLE);
    }
}
