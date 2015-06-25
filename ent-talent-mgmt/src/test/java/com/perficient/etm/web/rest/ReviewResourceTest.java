package com.perficient.etm.web.rest;

import com.perficient.etm.Application;
import com.perficient.etm.domain.Review;
import com.perficient.etm.repository.ReviewRepository;

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
import org.joda.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ReviewResource REST controller.
 *
 * @see ReviewResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ReviewResourceTest {

    private static final String DEFAULT_TITLE = "SAMPLE_TEXT";
    private static final String UPDATED_TITLE = "UPDATED_TEXT";

    private static final LocalDate DEFAULT_START_DATE = new LocalDate(0L);
    private static final LocalDate UPDATED_START_DATE = new LocalDate();

    private static final LocalDate DEFAULT_END_DATE = new LocalDate(0L);
    private static final LocalDate UPDATED_END_DATE = new LocalDate();
    private static final String DEFAULT_CLIENT = "SAMPLE_TEXT";
    private static final String UPDATED_CLIENT = "UPDATED_TEXT";
    private static final String DEFAULT_PROJECT = "SAMPLE_TEXT";
    private static final String UPDATED_PROJECT = "UPDATED_TEXT";
    private static final String DEFAULT_ROLE = "SAMPLE_TEXT";
    private static final String UPDATED_ROLE = "UPDATED_TEXT";
    private static final String DEFAULT_RESPONSIBILITIES = "SAMPLE_TEXT";
    private static final String UPDATED_RESPONSIBILITIES = "UPDATED_TEXT";

    private static final Double DEFAULT_RATING = 0.0;
    private static final Double UPDATED_RATING = 3.25;

    @Inject
    private ReviewRepository reviewRepository;

    private MockMvc restReviewMockMvc;

    private Review review;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReviewResource reviewResource = new ReviewResource();
        ReflectionTestUtils.setField(reviewResource, "reviewRepository", reviewRepository);
        this.restReviewMockMvc = MockMvcBuilders.standaloneSetup(reviewResource).build();
    }

    @Before
    public void initTest() {
        review = new Review();
        review.setTitle(DEFAULT_TITLE);
        review.setStartDate(DEFAULT_START_DATE);
        review.setEndDate(DEFAULT_END_DATE);
        review.setClient(DEFAULT_CLIENT);
        review.setProject(DEFAULT_PROJECT);
        review.setRole(DEFAULT_ROLE);
        review.setResponsibilities(DEFAULT_RESPONSIBILITIES);
        review.setRating(DEFAULT_RATING);
    }

    @Test
    @Transactional
    public void createReview() throws Exception {
        // Validate the database is empty
        assertThat(reviewRepository.findAll()).hasSize(0);

        // Create the Review
        restReviewMockMvc.perform(post("/api/reviews")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(review)))
                .andExpect(status().isOk());

        // Validate the Review in the database
        List<Review> reviews = reviewRepository.findAll();
        assertThat(reviews).hasSize(1);
        Review testReview = reviews.iterator().next();
        assertThat(testReview.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testReview.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testReview.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testReview.getClient()).isEqualTo(DEFAULT_CLIENT);
        assertThat(testReview.getProject()).isEqualTo(DEFAULT_PROJECT);
        assertThat(testReview.getRole()).isEqualTo(DEFAULT_ROLE);
        assertThat(testReview.getResponsibilities()).isEqualTo(DEFAULT_RESPONSIBILITIES);
        assertThat(testReview.getRating()).isEqualTo(DEFAULT_RATING);
    }

    @Test
    @Transactional
    public void getAllReviews() throws Exception {
        // Initialize the database
        reviewRepository.saveAndFlush(review);

        // Get all the reviews
        restReviewMockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(review.getId().intValue()))
                .andExpect(jsonPath("$.[0].title").value(DEFAULT_TITLE.toString()))
                .andExpect(jsonPath("$.[0].startDate").value(DEFAULT_START_DATE.toString()))
                .andExpect(jsonPath("$.[0].endDate").value(DEFAULT_END_DATE.toString()))
                .andExpect(jsonPath("$.[0].client").value(DEFAULT_CLIENT.toString()))
                .andExpect(jsonPath("$.[0].project").value(DEFAULT_PROJECT.toString()))
                .andExpect(jsonPath("$.[0].role").value(DEFAULT_ROLE.toString()))
                .andExpect(jsonPath("$.[0].responsibilities").value(DEFAULT_RESPONSIBILITIES.toString()))
                .andExpect(jsonPath("$.[0].rating").value(DEFAULT_RATING.doubleValue()));
    }

    @Test
    @Transactional
    public void getReview() throws Exception {
        // Initialize the database
        reviewRepository.saveAndFlush(review);

        // Get the review
        restReviewMockMvc.perform(get("/api/reviews/{id}", review.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(review.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.client").value(DEFAULT_CLIENT.toString()))
            .andExpect(jsonPath("$.project").value(DEFAULT_PROJECT.toString()))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()))
            .andExpect(jsonPath("$.responsibilities").value(DEFAULT_RESPONSIBILITIES.toString()))
            .andExpect(jsonPath("$.rating").value(DEFAULT_RATING.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingReview() throws Exception {
        // Get the review
        restReviewMockMvc.perform(get("/api/reviews/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateReview() throws Exception {
        // Initialize the database
        reviewRepository.saveAndFlush(review);

        // Update the review
        review.setTitle(UPDATED_TITLE);
        review.setStartDate(UPDATED_START_DATE);
        review.setEndDate(UPDATED_END_DATE);
        review.setClient(UPDATED_CLIENT);
        review.setProject(UPDATED_PROJECT);
        review.setRole(UPDATED_ROLE);
        review.setResponsibilities(UPDATED_RESPONSIBILITIES);
        review.setRating(UPDATED_RATING);
        restReviewMockMvc.perform(post("/api/reviews")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(review)))
                .andExpect(status().isOk());

        // Validate the Review in the database
        List<Review> reviews = reviewRepository.findAll();
        assertThat(reviews).hasSize(1);
        Review testReview = reviews.iterator().next();
        assertThat(testReview.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testReview.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testReview.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testReview.getClient()).isEqualTo(UPDATED_CLIENT);
        assertThat(testReview.getProject()).isEqualTo(UPDATED_PROJECT);
        assertThat(testReview.getRole()).isEqualTo(UPDATED_ROLE);
        assertThat(testReview.getResponsibilities()).isEqualTo(UPDATED_RESPONSIBILITIES);
        assertThat(testReview.getRating()).isEqualTo(UPDATED_RATING);
    }

    @Test
    @Transactional
    public void deleteReview() throws Exception {
        // Initialize the database
        reviewRepository.saveAndFlush(review);

        // Get the review
        restReviewMockMvc.perform(delete("/api/reviews/{id}", review.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Review> reviews = reviewRepository.findAll();
        assertThat(reviews).hasSize(0);
    }
}