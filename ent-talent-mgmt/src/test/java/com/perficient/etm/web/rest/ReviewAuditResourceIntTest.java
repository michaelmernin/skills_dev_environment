package com.perficient.etm.web.rest;

import com.perficient.etm.Application;
import com.perficient.etm.domain.ReviewAudit;
import com.perficient.etm.repository.ReviewAuditRepository;
import com.perficient.etm.utils.ResourceTestUtils;
import com.perficient.etm.utils.SpringAppTest;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
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
 * Test class for the ReviewAuditResource REST controller.
 *
 * @see ReviewAuditResource
 */
public class ReviewAuditResourceIntTest extends SpringAppTest {


    private static final Long DEFAULT_REVIEW_AUDIT_ID = 1L;
    private static final Long UPDATED_REVIEW_AUDIT_ID = 2L;

    private static final LocalDate DEFAULT_DATE = LocalDate.now();
    private static final LocalDate UPDATED_DATE = LocalDate.now().plusDays(1);
    private static final String DEFAULT_COMMENT = "AAAAA";
    private static final String UPDATED_COMMENT = "BBBBB";

    @Inject
    private ReviewAuditRepository reviewAuditRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restReviewAuditMockMvc;

    private ReviewAudit reviewAudit;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ReviewAuditResource reviewAuditResource = new ReviewAuditResource();
        ReflectionTestUtils.setField(reviewAuditResource, "reviewAuditRepository", reviewAuditRepository);
        this.restReviewAuditMockMvc = MockMvcBuilders.standaloneSetup(reviewAuditResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        reviewAudit = new ReviewAudit();
        reviewAudit.setReviewAuditId(DEFAULT_REVIEW_AUDIT_ID);
        reviewAudit.setDate(DEFAULT_DATE);
        reviewAudit.setComment(DEFAULT_COMMENT);
    }

    @Test
    @Transactional
    public void createReviewAudit() throws Exception {
        int databaseSizeBeforeCreate = reviewAuditRepository.findAll().size();

        // Create the ReviewAudit

        restReviewAuditMockMvc.perform(post("/api/reviewAudits")
                .contentType(ResourceTestUtils.APPLICATION_JSON_UTF8)
                .content(ResourceTestUtils.convertObjectToJsonBytes(reviewAudit)))
                .andExpect(status().isCreated());

        // Validate the ReviewAudit in the database
        List<ReviewAudit> reviewAudits = reviewAuditRepository.findAll();
        assertThat(reviewAudits).hasSize(databaseSizeBeforeCreate + 1);
        ReviewAudit testReviewAudit = reviewAudits.get(reviewAudits.size() - 1);
        assertThat(testReviewAudit.getReviewAuditId()).isEqualTo(DEFAULT_REVIEW_AUDIT_ID);
        //assertThat(testReviewAudit.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testReviewAudit.getComment()).isEqualTo(DEFAULT_COMMENT);
    }

    @Test
    @Transactional
    public void getAllReviewAudits() throws Exception {
        // Initialize the database
        reviewAuditRepository.saveAndFlush(reviewAudit);

        // Get all the reviewAudits
        restReviewAuditMockMvc.perform(get("/api/reviewAudits"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(reviewAudit.getId().intValue())))
                .andExpect(jsonPath("$.[*].reviewAuditId").value(hasItem(DEFAULT_REVIEW_AUDIT_ID.intValue())))
                .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
                .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())));
    }

    @Test
    @Transactional
    public void getReviewAudit() throws Exception {
        // Initialize the database
        reviewAuditRepository.saveAndFlush(reviewAudit);

        // Get the reviewAudit
        restReviewAuditMockMvc.perform(get("/api/reviewAudits/{id}", reviewAudit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(reviewAudit.getId().intValue()))
            .andExpect(jsonPath("$.reviewAuditId").value(DEFAULT_REVIEW_AUDIT_ID.intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingReviewAudit() throws Exception {
        // Get the reviewAudit
        restReviewAuditMockMvc.perform(get("/api/reviewAudits/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateReviewAudit() throws Exception {
        // Initialize the database
        reviewAuditRepository.saveAndFlush(reviewAudit);

		int databaseSizeBeforeUpdate = reviewAuditRepository.findAll().size();

        // Update the reviewAudit
        reviewAudit.setReviewAuditId(UPDATED_REVIEW_AUDIT_ID);
        reviewAudit.setDate(UPDATED_DATE);
        reviewAudit.setComment(UPDATED_COMMENT);

        restReviewAuditMockMvc.perform(put("/api/reviewAudits")
                .contentType(ResourceTestUtils.APPLICATION_JSON_UTF8)
                .content(ResourceTestUtils.convertObjectToJsonBytes(reviewAudit)))
                .andExpect(status().isOk());

        // Validate the ReviewAudit in the database
        List<ReviewAudit> reviewAudits = reviewAuditRepository.findAll();
        assertThat(reviewAudits).hasSize(databaseSizeBeforeUpdate);
        ReviewAudit testReviewAudit = reviewAudits.get(reviewAudits.size() - 1);
        assertThat(testReviewAudit.getReviewAuditId()).isEqualTo(UPDATED_REVIEW_AUDIT_ID);
        assertThat(testReviewAudit.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testReviewAudit.getComment()).isEqualTo(UPDATED_COMMENT);
    }

    @Test
    @Transactional
    public void deleteReviewAudit() throws Exception {
        // Initialize the database
        reviewAuditRepository.saveAndFlush(reviewAudit);

		int databaseSizeBeforeDelete = reviewAuditRepository.findAll().size();

        // Get the reviewAudit
        restReviewAuditMockMvc.perform(delete("/api/reviewAudits/{id}", reviewAudit.getId())
                .accept(ResourceTestUtils.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ReviewAudit> reviewAudits = reviewAuditRepository.findAll();
        assertThat(reviewAudits).hasSize(databaseSizeBeforeDelete - 1);
    }
}
