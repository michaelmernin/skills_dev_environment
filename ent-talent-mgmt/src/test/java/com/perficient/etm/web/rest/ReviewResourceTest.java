package com.perficient.etm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.ReviewType;
import com.perficient.etm.domain.User;
import com.perficient.etm.exception.ETMException;
import com.perficient.etm.repository.ReviewRepository;
import com.perficient.etm.repository.ReviewTypeRepository;
import com.perficient.etm.service.ReviewService;
import com.perficient.etm.utils.ResourceTestUtils;
import com.perficient.etm.utils.SpringAppTest;

/**
 * Test class for the ReviewResource REST controller.
 *
 * @see ReviewResource
 */
public class ReviewResourceTest extends SpringAppTest {

    private static final String DEFAULT_TITLE = "SAMPLE_TEXT";
    private static final String UPDATED_TITLE = "UPDATED_TEXT";

    private static final LocalDate DEFAULT_START_DATE = new LocalDate().now();
    private static final LocalDate UPDATED_START_DATE = new LocalDate().now();

    private static final LocalDate DEFAULT_END_DATE = DEFAULT_START_DATE
            .plusYears(1);
    private static final LocalDate UPDATED_END_DATE = UPDATED_START_DATE
            .plusYears(1);
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

    @Inject
    private ReviewService reviewService;

    @Inject
    private ReviewTypeRepository reviewTypeRepository;
    
    private MockMvc restReviewMockMvc;

    private Review review;

    @PostConstruct
    public void setup() throws ETMException {
        MockitoAnnotations.initMocks(this);
        ReviewResource reviewResource = new ReviewResource();
        ReflectionTestUtils.setField(reviewResource, "reviewSvc", reviewService);

        this.restReviewMockMvc = ResourceTestUtils.exceptionHandlingMockMvc(reviewResource).build();
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
        User reviewee = new User();
        reviewee.setId(7L);
        review.setReviewee(reviewee);
        ReviewType revType = reviewTypeRepository.findOne(1L);
        review.setReviewType(revType);
    }

    @Test
    @WithUserDetails("dev.user2")
    public void createReview() throws Exception {
        int count = (int) reviewRepository.count();

        // Create the Review
        restReviewMockMvc
                .perform(post("/api/reviews")
                        .contentType(ResourceTestUtils.APPLICATION_JSON_UTF8)
                        .content(ResourceTestUtils
                                .convertObjectToJsonBytes(review)))
                .andExpect(status().isCreated());

        // Validate the Review in the database
        int newCount = (int) reviewRepository.count();
        List<Review> reviews = reviewRepository.findAll();
        assertThat(newCount).isEqualTo(count + 1);
        Optional<Review> optional = reviews.stream().filter(r -> {
            return DEFAULT_TITLE.equals(r.getTitle());
        }).findAny();
        assertThat(optional.isPresent()).isTrue();
        Review testReview = optional.get();
        assertThat(testReview.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testReview.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testReview.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testReview.getClient()).isEqualTo(DEFAULT_CLIENT);
        assertThat(testReview.getProject()).isEqualTo(DEFAULT_PROJECT);
        assertThat(testReview.getRole()).isEqualTo(DEFAULT_ROLE);
        assertThat(testReview.getResponsibilities())
                .isEqualTo(DEFAULT_RESPONSIBILITIES);
        assertThat(testReview.getRating()).isEqualTo(DEFAULT_RATING);
    }

    @Test
    @WithUserDetails("dev.user2")
    public void getAllReviews() throws Exception {
        int count = (int) reviewRepository.count();

        Review review = reviewRepository.findOne(1L);

        // Get all the reviews
        ResultActions result = restReviewMockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(review.getId().intValue()))
                .andExpect(jsonPath("$[0].title").value(review.getTitle()))
                .andExpect(jsonPath("$[0].startDate")
                        .value(review.getStartDate().toString()))
                .andExpect(jsonPath("$[0].endDate")
                        .value(review.getEndDate().toString()))
                .andExpect(jsonPath("$[0].client").value(review.getClient()))
                .andExpect(jsonPath("$[0].project").value(review.getProject()))
                .andExpect(jsonPath("$[0].role").value(review.getRole()))
                .andExpect(jsonPath("$[0].responsibilities")
                        .value(review.getResponsibilities()))
                .andExpect(jsonPath("$[0].rating").value(review.getRating()));

        ResourceTestUtils.assertJsonCount(result, count);
    }

    // @Test
    @WithUserDetails("dev.user2")
    public void getReview() throws Exception {
        // Initialize the database
        reviewRepository.saveAndFlush(review);

        // Get the review
        restReviewMockMvc.perform(get("/api/reviews/{id}", review.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(review.getId().intValue()))
                .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
                .andExpect(jsonPath("$.startDate")
                        .value(DEFAULT_START_DATE.toString()))
                .andExpect(jsonPath("$.endDate")
                        .value(DEFAULT_END_DATE.toString()))
                .andExpect(
                        jsonPath("$.client").value(DEFAULT_CLIENT.toString()))
                .andExpect(
                        jsonPath("$.project").value(DEFAULT_PROJECT.toString()))
                .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()))
                .andExpect(jsonPath("$.responsibilities")
                        .value(DEFAULT_RESPONSIBILITIES.toString()))
                .andExpect(jsonPath("$.rating")
                        .value(DEFAULT_RATING.doubleValue()));
    }

    // @Test
    @WithUserDetails("dev.user2")
    public void getNonExistingReview() throws Exception {
        // Get the review
        restReviewMockMvc.perform(get("/api/reviews/{id}", 404L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails("dev.user4")
    public void getNonAuthorizedReview() throws Exception {
        // dev.user4 does not have access to review 2 (not gm, reviewee,
        // reviewer, councelor or peer on review 2)
        restReviewMockMvc.perform(get("/api/reviews/{id}", 2L))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("dev.user4")
    public void getAuthorizedReview() throws Exception {
        // dev.user4 does not have access to review 2 (not gm, reviewee,
        // reviewer, councelor or peer on review 2)
        restReviewMockMvc.perform(get("/api/reviews/{id}", 3L))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("dev.user2")
    @Ignore
    public void deleteReview() throws Exception {
        int count = (int) reviewRepository.count();
        Review review = reviewRepository.findOne(1L);

        // Get the review
        restReviewMockMvc
                .perform(delete("/api/reviews/{id}", review.getId())
                        .accept(ResourceTestUtils.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Review> reviews = reviewRepository.findAll();
        assertThat(reviews).hasSize(count - 1);
    }
}
