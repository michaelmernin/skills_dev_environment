package com.perficient.etm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.perficient.etm.domain.Feedback;
import com.perficient.etm.domain.FeedbackStatus;
import com.perficient.etm.domain.FeedbackType;
import com.perficient.etm.domain.Rating;
import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.User;
import com.perficient.etm.repository.FeedbackRepository;
import com.perficient.etm.repository.RatingRepository;
import com.perficient.etm.utils.ResourceTestUtils;
import com.perficient.etm.utils.SpringAppTest;

/**
 * Test class for the RatingResource REST controller.
 *
 * @see RatingResource
 */
public class RatingResourceTest extends SpringAppTest {

    private static final long AUTHOR_ID = 8L;
    private static final String AUTHOR_LOGIN = "dev.user8";
    private static final long REVIEW_ID = 1L;
    private static final long FEEDBACK_ID = 1L;
    private static final Double DEFAULT_SCORE = 0.0;
    private static final Double UPDATED_SCORE = 3.25;
    private static final String DEFAULT_COMMENT = "SAMPLE_TEXT";
    private static final String UPDATED_COMMENT = "UPDATED_TEXT";
    

    private static final Boolean DEFAULT_VISIBLE = false;
    private static final Boolean UPDATED_VISIBLE = true;

    @Inject
    private RatingRepository ratingRepository;
    
    @Inject
    private FeedbackRepository feedbackRepository;

    private MockMvc restRatingMockMvc;

    private Rating rating;
    
    private Feedback feedback;
    
    private Review review;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RatingResource ratingResource = new RatingResource();
        ReflectionTestUtils.setField(ratingResource, "ratingRepository", ratingRepository);
        ReflectionTestUtils.setField(ratingResource, "feedbackRepository", feedbackRepository);
        this.restRatingMockMvc = MockMvcBuilders.standaloneSetup(ratingResource).build();
    }

    @Before
    public void initTest() {
        // author: dev.user8 - id: 8
        User author = new User();
        author.setId(AUTHOR_ID);
        author.setLogin(AUTHOR_LOGIN);
        // rating
        rating = new Rating();
        rating.setFeedback(feedback);
        rating.setScore(DEFAULT_SCORE);
        rating.setComment(DEFAULT_COMMENT);
        rating.setVisible(DEFAULT_VISIBLE);
        // feedback
        feedback = new Feedback();
        feedback.setReview(review);
        feedback.setFeedbackType(FeedbackType.PEER);
        feedback.setFeedbackStatus(FeedbackStatus.OPEN);
        feedback.setAuthor(author);
        Set<Rating> ratings = new HashSet<Rating>(Arrays.asList(rating));
        feedback.setRatings(ratings);
        // review
        Review review = new Review();
        review.setId(REVIEW_ID);
        review.setFeedback(new HashSet<>(Arrays.asList(feedback)));
    }

    @Test
    @Transactional
    @WithUserDetails("dev.user8")
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
    @WithUserDetails("dev.user8")
    public void getAllRatings() throws Exception {
        // Initialize the database
        Feedback savedfeedback = feedbackRepository.save(feedback);
        Rating savedRating = ratingRepository.save(rating);

        // Get all the ratings
        restRatingMockMvc.perform(get("/api/reviews/{reviewId}/feedback/{feedbackId}/ratings", REVIEW_ID, savedfeedback.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(savedRating.getId().intValue())))
                .andExpect(jsonPath("$.[*].score").value(hasItem(DEFAULT_SCORE.doubleValue())))
                .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
                .andExpect(jsonPath("$.[*].visible").value(hasItem(DEFAULT_VISIBLE.booleanValue())));
    }

    @Test
    @Transactional
    @WithUserDetails("dev.user8")
    public void getRating() throws Exception {
        // Initialize the database
        Feedback savedfeedback = feedbackRepository.saveAndFlush(feedback);
        ratingRepository.saveAndFlush(rating);

        // Get the rating
        restRatingMockMvc.perform(get("/api/reviews/{reviewId}/feedback/{feedbackId}/ratings/{id}", REVIEW_ID, savedfeedback.getId(), rating.getId()))
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
    @WithUserDetails("dev.user8")
    public void updateRating() throws Exception {
        // Initialize the database
        Feedback savedfeedback = feedbackRepository.save(feedback);
        Rating savedRating = ratingRepository.saveAndFlush(rating);

        int count = (int) ratingRepository.count();

        // Update the rating
        rating.setScore(UPDATED_SCORE);
        rating.setComment(UPDATED_COMMENT);
        rating.setVisible(UPDATED_VISIBLE);
        restRatingMockMvc.perform(put("/api/reviews/{reviewId}/feedback/{feedbackId}/ratings/{ratingId}", REVIEW_ID, savedfeedback.getId(), savedRating.getId())
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
