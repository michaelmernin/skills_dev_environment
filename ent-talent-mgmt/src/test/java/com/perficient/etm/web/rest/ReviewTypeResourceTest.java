package com.perficient.etm.web.rest;

import com.perficient.etm.Application;
import com.perficient.etm.domain.ReviewType;
import com.perficient.etm.repository.ReviewTypeRepository;

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
 * Test class for the ReviewTypeResource REST controller.
 *
 * @see ReviewTypeResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ReviewTypeResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";

    @Inject
    private ReviewTypeRepository reviewTypeRepository;

    private MockMvc restReviewTypeMockMvc;

    private ReviewType reviewType;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReviewTypeResource reviewTypeResource = new ReviewTypeResource();
        ReflectionTestUtils.setField(reviewTypeResource, "reviewTypeRepository", reviewTypeRepository);
        this.restReviewTypeMockMvc = MockMvcBuilders.standaloneSetup(reviewTypeResource).build();
    }

    @Before
    public void initTest() {
        reviewType = new ReviewType();
        reviewType.setName(DEFAULT_NAME);
        reviewType.setDescription(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createReviewType() throws Exception {
        // Validate the database is empty
        assertThat(reviewTypeRepository.findAll()).hasSize(0);

        // Create the ReviewType
        restReviewTypeMockMvc.perform(post("/api/reviewTypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(reviewType)))
                .andExpect(status().isOk());

        // Validate the ReviewType in the database
        List<ReviewType> reviewTypes = reviewTypeRepository.findAll();
        assertThat(reviewTypes).hasSize(1);
        ReviewType testReviewType = reviewTypes.iterator().next();
        assertThat(testReviewType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testReviewType.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllReviewTypes() throws Exception {
        // Initialize the database
        reviewTypeRepository.saveAndFlush(reviewType);

        // Get all the reviewTypes
        restReviewTypeMockMvc.perform(get("/api/reviewTypes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(reviewType.getId().intValue()))
                .andExpect(jsonPath("$.[0].name").value(DEFAULT_NAME.toString()))
                .andExpect(jsonPath("$.[0].description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getReviewType() throws Exception {
        // Initialize the database
        reviewTypeRepository.saveAndFlush(reviewType);

        // Get the reviewType
        restReviewTypeMockMvc.perform(get("/api/reviewTypes/{id}", reviewType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(reviewType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingReviewType() throws Exception {
        // Get the reviewType
        restReviewTypeMockMvc.perform(get("/api/reviewTypes/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateReviewType() throws Exception {
        // Initialize the database
        reviewTypeRepository.saveAndFlush(reviewType);

        // Update the reviewType
        reviewType.setName(UPDATED_NAME);
        reviewType.setDescription(UPDATED_DESCRIPTION);
        restReviewTypeMockMvc.perform(post("/api/reviewTypes")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(reviewType)))
                .andExpect(status().isOk());

        // Validate the ReviewType in the database
        List<ReviewType> reviewTypes = reviewTypeRepository.findAll();
        assertThat(reviewTypes).hasSize(1);
        ReviewType testReviewType = reviewTypes.iterator().next();
        assertThat(testReviewType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testReviewType.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void deleteReviewType() throws Exception {
        // Initialize the database
        reviewTypeRepository.saveAndFlush(reviewType);

        // Get the reviewType
        restReviewTypeMockMvc.perform(delete("/api/reviewTypes/{id}", reviewType.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ReviewType> reviewTypes = reviewTypeRepository.findAll();
        assertThat(reviewTypes).hasSize(0);
    }
}
