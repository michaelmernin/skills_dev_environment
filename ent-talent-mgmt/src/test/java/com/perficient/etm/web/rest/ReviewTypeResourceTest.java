package com.perficient.etm.web.rest;

import com.perficient.etm.domain.ReviewType;
import com.perficient.etm.repository.ReviewTypeRepository;
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
 * Test class for the ReviewTypeResource REST controller.
 *
 * @see ReviewTypeResource
 */
public class ReviewTypeResourceTest extends SpringAppTest {

    private static final Boolean DEFAULT_ACTIVE = true;
    private static final Boolean UPDATED_ACTIVE = false;
    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";
    private static final Integer DEFAULT_VERSION = 1;
    private static final Integer UPDATED_VERSION = 2;

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
        reviewType.setVersion(DEFAULT_VERSION);
        reviewType.setActive(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    public void createReviewType() throws Exception {
        int count = (int) reviewTypeRepository.count();

        // Create the ReviewType
        restReviewTypeMockMvc.perform(post("/api/reviewTypes")
                .contentType(ResourceTestUtils.APPLICATION_JSON_UTF8)
                .content(ResourceTestUtils.convertObjectToJsonBytes(reviewType)))
                .andExpect(status().isOk());

        // Validate the ReviewType in the database
        List<ReviewType> reviewTypes = reviewTypeRepository.findAll();
        assertThat(reviewTypes).hasSize(count + 1);
        Optional<ReviewType> optional = reviewTypes.stream().filter(rt -> {return DEFAULT_NAME.equals(rt.getName());}).findAny();
        assertThat(optional.isPresent()).isTrue();
        ReviewType testReviewType = optional.get();
        assertThat(testReviewType.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testReviewType.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testReviewType.getVersion()).isEqualTo(DEFAULT_VERSION);
        assertThat(testReviewType.isActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    public void getAllReviewTypes() throws Exception {
        // Read a review type
        ReviewType reviewType = reviewTypeRepository.findOne(1L);

        // Get all the reviewTypes
        restReviewTypeMockMvc.perform(get("/api/reviewTypes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(reviewType.getId().intValue()))
                .andExpect(jsonPath("$.[0].name").value(reviewType.getName()))
                .andExpect(jsonPath("$.[0].description").value(reviewType.getDescription()))
                .andExpect(jsonPath("$.[0].version").value(reviewType.getVersion().intValue()))
                .andExpect(jsonPath("$.[0].active").value(reviewType.isActive().booleanValue()));
    }

    @Test
    @Transactional
    public void getReviewType() throws Exception {
        // Read a review type
        ReviewType reviewType = reviewTypeRepository.findOne(1L);

        // Get the reviewType
        restReviewTypeMockMvc.perform(get("/api/reviewTypes/{id}", reviewType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(reviewType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(reviewType.getName()))
            .andExpect(jsonPath("$.description").value(reviewType.getDescription()))
            .andExpect(jsonPath("$.version").value(reviewType.getVersion().intValue()))
            .andExpect(jsonPath("$.active").value(reviewType.isActive().booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingReviewType() throws Exception {
        // Get the reviewType
        restReviewTypeMockMvc.perform(get("/api/reviewTypes/{id}", 404L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateReviewType() throws Exception {
        int count = (int) reviewTypeRepository.count();
        
        // Read a review type
        ReviewType reviewType = reviewTypeRepository.findOne(1L);

        // Update the reviewType
        reviewType.setName(UPDATED_NAME);
        reviewType.setDescription(UPDATED_DESCRIPTION);
        reviewType.setVersion(UPDATED_VERSION);
        reviewType.setActive(UPDATED_ACTIVE);
        restReviewTypeMockMvc.perform(post("/api/reviewTypes")
                .contentType(ResourceTestUtils.APPLICATION_JSON_UTF8)
                .content(ResourceTestUtils.convertObjectToJsonBytes(reviewType)))
                .andExpect(status().isOk());

        // Validate the ReviewType in the database
        List<ReviewType> reviewTypes = reviewTypeRepository.findAll();
        assertThat(reviewTypes).hasSize(count);
        Optional<ReviewType> optional = reviewTypes.stream().filter(rt -> {return rt.getId() == 1L;}).findAny();
        assertThat(optional.isPresent()).isTrue();
        ReviewType testReviewType = optional.get();
        assertThat(testReviewType.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testReviewType.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testReviewType.getVersion()).isEqualTo(UPDATED_VERSION);
        assertThat(testReviewType.isActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    public void deleteReviewType() throws Exception {
        int count = (int) reviewTypeRepository.count();
        
        // Read a review type
        ReviewType reviewType = reviewTypeRepository.findOne(1L);

        // Get the reviewType
        restReviewTypeMockMvc.perform(delete("/api/reviewTypes/{id}", reviewType.getId())
                .accept(ResourceTestUtils.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ReviewType> reviewTypes = reviewTypeRepository.findAll();
        assertThat(reviewTypes).hasSize(count - 1);
    }
}
