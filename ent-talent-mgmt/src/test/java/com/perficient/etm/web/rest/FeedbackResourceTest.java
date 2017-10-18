package com.perficient.etm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.perficient.etm.domain.Feedback;
import com.perficient.etm.domain.FeedbackStatus;
import com.perficient.etm.domain.FeedbackType;
import com.perficient.etm.domain.Question;
import com.perficient.etm.domain.Rating;
import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.User;
import com.perficient.etm.repository.FeedbackRepository;
import com.perficient.etm.utils.ResourceTestUtils;
import com.perficient.etm.utils.SpringAppTest;

/**
 * Test class for the FeedbackResource REST controller.
 *
 * @see FeedbackResource
 */
public class FeedbackResourceTest extends SpringAppTest {

    private static final long AUTHOR_ID = 8L;

    private static final long REVIEW_ID = 1L;

    private static final int NUM_RATINGS = 21;

    @Inject
    private FeedbackRepository feedbackRepository;


    private MockMvc restFeedbackMockMvc;

    private Feedback feedback;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        FeedbackResource feedbackResource = new FeedbackResource();
        ReflectionTestUtils.setField(feedbackResource, "feedbackRepository", feedbackRepository);

        this.restFeedbackMockMvc = ResourceTestUtils.exceptionHandlingMockMvc(feedbackResource).build();
    }

    @Before
    public void initTest() {
        feedback = new Feedback();
        Review review = new Review();
        review.setId(REVIEW_ID);
        feedback.setReview(review);
        feedback.setFeedbackType(FeedbackType.PEER);
        feedback.setFeedbackStatus(FeedbackStatus.OPEN);
        User author = new User();
        author.setId(AUTHOR_ID);
        author.setLogin("dev.user8");
        feedback.setAuthor(author);
        feedback.setRatings(getRatings());
    }

    @Test
    @Ignore
    @Transactional
    @WithUserDetails("dev.user8")
    public void getAllFeedback() throws Exception {
        // Initialize the database
        feedbackRepository.saveAndFlush(feedback);

        // Get all the feedback
        restFeedbackMockMvc.perform(get("/api/reviews/{reviewId}/feedback", REVIEW_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].id").value(hasItem(feedback.getId().intValue())));
    }

    @Test
    @Ignore
    @Transactional
    @WithUserDetails("dev.user8")
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
    @Ignore
    @Transactional
    @WithUserDetails("dev.user2")
    public void getNonExistingFeedback() throws Exception {
        // Get the feedback
        restFeedbackMockMvc.perform(get("/api/reviews/{reviewId}/feedback/{id}", REVIEW_ID, Long.MAX_VALUE))
                .andExpect(status().isForbidden());
    }

    @Test
    @Ignore
    @Transactional
    @WithUserDetails("dev.user8")
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

    private Set<Rating> getRatings() {
        return LongStream.range(1, NUM_RATINGS + 1).mapToObj(id -> {
          Question question = new Question();
          question.setId(id);
          return question;
        }).map(q -> {
            Rating rating = new Rating();
            rating.setQuestion(q);
            rating.setScore(3.0);
            return rating;
        }).collect(Collectors.toSet());
    }
}
