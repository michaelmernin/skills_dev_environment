package com.perficient.etm.web.rest;

import com.perficient.etm.Application;
import com.perficient.etm.domain.Category;
import com.perficient.etm.repository.CategoryRepository;

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
 * Test class for the CategoryResource REST controller.
 *
 * @see CategoryResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class CategoryResourceTest {

    private static final String DEFAULT_TITLE = "SAMPLE_TEXT";
    private static final String UPDATED_TITLE = "UPDATED_TEXT";

    @Inject
    private CategoryRepository categoryRepository;

    private MockMvc restCategoryMockMvc;

    private Category category;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CategoryResource categoryResource = new CategoryResource();
        ReflectionTestUtils.setField(categoryResource, "categoryRepository", categoryRepository);
        this.restCategoryMockMvc = MockMvcBuilders.standaloneSetup(categoryResource).build();
    }

    @Before
    public void initTest() {
        category = new Category();
        category.setTitle(DEFAULT_TITLE);
    }

    @Test
    @Transactional
    public void createCategory() throws Exception {
        // Validate the database is empty
        assertThat(categoryRepository.findAll()).hasSize(0);

        // Create the Category
        restCategoryMockMvc.perform(post("/api/categories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(category)))
                .andExpect(status().isOk());

        // Validate the Category in the database
        List<Category> categories = categoryRepository.findAll();
        assertThat(categories).hasSize(1);
        Category testCategory = categories.iterator().next();
        assertThat(testCategory.getTitle()).isEqualTo(DEFAULT_TITLE);
    }

    @Test
    @Transactional
    public void getAllCategories() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get all the categories
        restCategoryMockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(category.getId().intValue()))
                .andExpect(jsonPath("$.[0].title").value(DEFAULT_TITLE.toString()));
    }

    @Test
    @Transactional
    public void getCategory() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get the category
        restCategoryMockMvc.perform(get("/api/categories/{id}", category.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(category.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCategory() throws Exception {
        // Get the category
        restCategoryMockMvc.perform(get("/api/categories/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCategory() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Update the category
        category.setTitle(UPDATED_TITLE);
        restCategoryMockMvc.perform(post("/api/categories")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(category)))
                .andExpect(status().isOk());

        // Validate the Category in the database
        List<Category> categories = categoryRepository.findAll();
        assertThat(categories).hasSize(1);
        Category testCategory = categories.iterator().next();
        assertThat(testCategory.getTitle()).isEqualTo(UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void deleteCategory() throws Exception {
        // Initialize the database
        categoryRepository.saveAndFlush(category);

        // Get the category
        restCategoryMockMvc.perform(delete("/api/categories/{id}", category.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Category> categories = categoryRepository.findAll();
        assertThat(categories).hasSize(0);
    }
}