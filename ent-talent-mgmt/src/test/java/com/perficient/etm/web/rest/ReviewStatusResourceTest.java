package com.perficient.etm.web.rest;

import com.perficient.etm.Application;
import com.perficient.etm.domain.ReviewStatus;
import com.perficient.etm.repository.ReviewStatusRepository;

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
 * Test class for the ReviewStatusResource REST controller.
 *
 * @see ReviewStatusResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ReviewStatusResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";

    @Inject
    private ReviewStatusRepository reviewStatusRepository;

    private MockMvc restReviewStatusMockMvc;

    private ReviewStatus reviewStatus;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReviewStatusResource reviewStatusResource = new ReviewStatusResource();
        ReflectionTestUtils.setField(reviewStatusResource, "reviewStatusRepository", reviewStatusRepository);
        this.restReviewStatusMockMvc = MockMvcBuilders.standaloneSetup(reviewStatusResource).build();
    }

    @Before
    public void initTest() {
        reviewStatus = new ReviewStatus();
        reviewStatus.setName(DEFAULT_NAME);
        reviewStatus.setDescription(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createReviewStatus() throws Exception {
        // Validate the database is empty
        assertThat(reviewStatusRepository.findAll()).hasSize(0);

        // Create the ReviewStatus
        restReviewStatusMockMvc.perform(post("/api/reviewStatuses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(reviewStatus)))
                .andExpect(status().isOk());

        // Validate the ReviewStatus in the database
        List<ReviewStatus> reviewStatuses = reviewStatusRepository.findAll();
        assertThat(reviewStatuses).hasSize(1);
        ReviewStatus testReviewStatus = reviewStatuses.iterator().next();
        assertThat(testReviewStatus.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testReviewStatus.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllReviewStatuses() throws Exception {
        // Initialize the database
        reviewStatusRepository.saveAndFlush(reviewStatus);

        // Get all the reviewStatuses
        restReviewStatusMockMvc.perform(get("/api/reviewStatuses"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(reviewStatus.getId().intValue()))
                .andExpect(jsonPath("$.[0].name").value(DEFAULT_NAME.toString()))
                .andExpect(jsonPath("$.[0].description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getReviewStatus() throws Exception {
        // Initialize the database
        reviewStatusRepository.saveAndFlush(reviewStatus);

        // Get the reviewStatus
        restReviewStatusMockMvc.perform(get("/api/reviewStatuses/{id}", reviewStatus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(reviewStatus.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingReviewStatus() throws Exception {
        // Get the reviewStatus
        restReviewStatusMockMvc.perform(get("/api/reviewStatuses/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateReviewStatus() throws Exception {
        // Initialize the database
        reviewStatusRepository.saveAndFlush(reviewStatus);

        // Update the reviewStatus
        reviewStatus.setName(UPDATED_NAME);
        reviewStatus.setDescription(UPDATED_DESCRIPTION);
        restReviewStatusMockMvc.perform(post("/api/reviewStatuses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(reviewStatus)))
                .andExpect(status().isOk());

        // Validate the ReviewStatus in the database
        List<ReviewStatus> reviewStatuses = reviewStatusRepository.findAll();
        assertThat(reviewStatuses).hasSize(1);
        ReviewStatus testReviewStatus = reviewStatuses.iterator().next();
        assertThat(testReviewStatus.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testReviewStatus.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void deleteReviewStatus() throws Exception {
        // Initialize the database
        reviewStatusRepository.saveAndFlush(reviewStatus);

        // Get the reviewStatus
        restReviewStatusMockMvc.perform(delete("/api/reviewStatuses/{id}", reviewStatus.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ReviewStatus> reviewStatuses = reviewStatusRepository.findAll();
        assertThat(reviewStatuses).hasSize(0);
    }
}
