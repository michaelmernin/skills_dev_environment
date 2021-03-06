package com.perficient.etm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.perficient.etm.domain.Goal;
import com.perficient.etm.domain.Review;
import com.perficient.etm.repository.GoalRepository;
import com.perficient.etm.repository.ReviewRepository;
import com.perficient.etm.utils.ResourceTestUtils;
import com.perficient.etm.utils.SpringAppTest;

/**
 * Test class for the GoalResource REST controller.
 *
 * @see GoalResource
 */
public class GoalResourceTest extends SpringAppTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_COMMENT = "SAMPLE_TEXT";
    private static final String UPDATED_COMMENT = "UPDATED_TEXT";

    private static final LocalDate DEFAULT_TARGET_DATE = new LocalDate(0L);
    private static final LocalDate UPDATED_TARGET_DATE = new LocalDate();

    private static final LocalDate DEFAULT_COMPLETION_DATE = new LocalDate(0L);
    private static final LocalDate UPDATED_COMPLETION_DATE = new LocalDate();

    private static final long REVIEW_ID = 1L;

    @Inject
    private GoalRepository goalRepository;

    @Inject
    private ReviewRepository reviewRepository;

    private MockMvc restGoalMockMvc;

    private Goal goal;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        GoalResource goalResource = new GoalResource();
        ReflectionTestUtils.setField(goalResource, "goalRepository", goalRepository);
        ReflectionTestUtils.setField(goalResource, "reviewRepository", reviewRepository);
        this.restGoalMockMvc = MockMvcBuilders.standaloneSetup(goalResource).build();
    }

    @Before
    public void initTest() {
        goal = new Goal();
        goal.setName(DEFAULT_NAME);
        goal.setReviewerComment(DEFAULT_COMMENT);
        goal.setEmployeeComment(DEFAULT_COMMENT);
        goal.setTargetDate(DEFAULT_TARGET_DATE);
        goal.setCompletionDate(DEFAULT_COMPLETION_DATE);
        goal.setReview(new Review());
        goal.getReview().setId(1L);
    }

    @Test
    @Ignore
    @Transactional
    @WithUserDetails("dev.user4")
    public void createGoal() throws Exception {
        int databaseSizeBeforeCreate = goalRepository.findAll().size();

        // Create the Goal
        restGoalMockMvc.perform(post("/api/reviews/{reviewId}/goals", REVIEW_ID)
                .contentType(ResourceTestUtils.APPLICATION_JSON_UTF8)
                .content(ResourceTestUtils.convertObjectToJsonBytes(goal)))
                .andExpect(status().isCreated());

        // Validate the Goal in the database
        List<Goal> goals = goalRepository.findAll();
        assertThat(goals).hasSize(databaseSizeBeforeCreate + 1);
        Goal testGoal = goals.get(goals.size() - 1);
        assertThat(testGoal.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testGoal.getReviewerComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testGoal.getEmployeeComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testGoal.getTargetDate()).isEqualTo(DEFAULT_TARGET_DATE);
        assertThat(testGoal.getCompletionDate()).isEqualTo(DEFAULT_COMPLETION_DATE);
    }

    @Test
    @Transactional
    public void getAllGoals() throws Exception {
        // Initialize the database
        goalRepository.saveAndFlush(goal);

        // Get all the goals
        restGoalMockMvc.perform(get("/api/reviews/{reviewId}/goals", goal.getReview().getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(goal.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].reviewerComment").value(hasItem(DEFAULT_COMMENT.toString())))
                .andExpect(jsonPath("$.[*].employeeComment").value(hasItem(DEFAULT_COMMENT.toString())))
                .andExpect(jsonPath("$.[*].targetDate").value(hasItem(DEFAULT_TARGET_DATE.toString())))
                .andExpect(jsonPath("$.[*].completionDate").value(hasItem(DEFAULT_COMPLETION_DATE.toString())));
    }

    @Test
    @Transactional
    public void getGoal() throws Exception {
        // Initialize the database
        goalRepository.saveAndFlush(goal);

        // Get the goal
        restGoalMockMvc.perform(get("/api/reviews/{reviewId}/goals/{id}", goal.getReview().getId(), goal.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(goal.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.targetDate").value(DEFAULT_TARGET_DATE.toString()))
            .andExpect(jsonPath("$.completionDate").value(DEFAULT_COMPLETION_DATE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingGoal() throws Exception {
        // Get the goal
        restGoalMockMvc.perform(get("/api/goals/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Ignore
    @Transactional
    @WithUserDetails("dev.user4")
    public void updateGoal() throws Exception {
        // Initialize the database
        goalRepository.saveAndFlush(goal);

        int databaseSizeBeforeUpdate = goalRepository.findAll().size();

        // Update the goal
        goal.setName(UPDATED_NAME);
        goal.setReviewerComment(UPDATED_COMMENT);
        goal.setEmployeeComment(UPDATED_COMMENT);
        goal.setTargetDate(UPDATED_TARGET_DATE);
        goal.setCompletionDate(UPDATED_COMPLETION_DATE);
        restGoalMockMvc.perform(put("/api/reviews/{reviewId}/goals", goal.getReview().getId())
                .contentType(ResourceTestUtils.APPLICATION_JSON_UTF8)
                .content(ResourceTestUtils.convertObjectToJsonBytes(goal)))
                .andExpect(status().isOk());

        // Validate the Goal in the database
        List<Goal> goals = goalRepository.findAll();
        assertThat(goals).hasSize(databaseSizeBeforeUpdate);
        Goal testGoal = goals.get(goals.size() - 1);
        assertThat(testGoal.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testGoal.getReviewerComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testGoal.getEmployeeComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testGoal.getTargetDate()).isEqualTo(UPDATED_TARGET_DATE);
        assertThat(testGoal.getCompletionDate()).isEqualTo(UPDATED_COMPLETION_DATE);
    }

    @Test
    @Ignore
    @Transactional
    @WithUserDetails("dev.user4")
    public void deleteGoal() throws Exception {
        // Initialize the database
        goalRepository.saveAndFlush(goal);

        int databaseSizeBeforeDelete = goalRepository.findAll().size();

        // Get the goal
        restGoalMockMvc.perform(delete("/api/reviews/{reviewId}/goals/{id}", goal.getReview().getId(), goal.getId())
                .accept(ResourceTestUtils.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Goal> goals = goalRepository.findAll();
        assertThat(goals).hasSize(databaseSizeBeforeDelete - 1);
    }
}
