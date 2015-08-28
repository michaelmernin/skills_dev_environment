package com.perficient.etm.web.rest;

import com.perficient.etm.Application;
import com.perficient.etm.domain.Category;
import com.perficient.etm.repository.CategoryRepository;
import com.perficient.etm.utils.ResourceTestUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
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
 * Test class for the CategoryResource REST controller.
 *
 * @see CategoryResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
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
        int count = (int) categoryRepository.count();

        // Create the Category
        restCategoryMockMvc.perform(post("/api/categories")
                .contentType(ResourceTestUtils.APPLICATION_JSON_UTF8)
                .content(ResourceTestUtils.convertObjectToJsonBytes(category)))
                .andExpect(status().isOk());

        // Validate the Category in the database
        List<Category> categories = categoryRepository.findAll();
        assertThat(categories).hasSize(count + 1);
        Optional<Category> optional = categories.stream().filter(c -> {return DEFAULT_TITLE.equals(c.getTitle());}).findAny();
        assertThat(optional.isPresent()).isTrue();
        Category testCategory = optional.get();
        assertThat(testCategory.getTitle()).isEqualTo(DEFAULT_TITLE);
    }

    @Test
    @Transactional
    public void getAllCategories() throws Exception {
        // Read a category
        Category category = categoryRepository.findOne(1L);

        // Get all the categories
        restCategoryMockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(category.getId().intValue()))
                .andExpect(jsonPath("$.[0].title").value(category.getTitle()));
    }

    @Test
    @Transactional
    public void getCategory() throws Exception {
        // Read a category
        Category category = categoryRepository.findOne(1L);

        // Get the category
        restCategoryMockMvc.perform(get("/api/categories/{id}", category.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(category.getId().intValue()))
            .andExpect(jsonPath("$.title").value(category.getTitle()));
    }

    @Test
    @Transactional
    public void getNonExistingCategory() throws Exception {
        // Get the category
        restCategoryMockMvc.perform(get("/api/categories/{id}", 404L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCategory() throws Exception {
        int count = (int) categoryRepository.count();
        
        // Read a category
        Category category = categoryRepository.findOne(1L);

        // Update the category
        category.setTitle(UPDATED_TITLE);
        restCategoryMockMvc.perform(post("/api/categories")
                .contentType(ResourceTestUtils.APPLICATION_JSON_UTF8)
                .content(ResourceTestUtils.convertObjectToJsonBytes(category)))
                .andExpect(status().isOk());

        // Validate the Category in the database
        List<Category> categories = categoryRepository.findAll();
        assertThat(categories).hasSize(count);
        Optional<Category> optional = categories.stream().filter(c -> {return c.getId() == 1L;}).findAny();
        assertThat(optional.isPresent()).isTrue();
        Category testCategory = optional.get();
        assertThat(testCategory.getTitle()).isEqualTo(UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void deleteCategory() throws Exception {
        int count = (int) categoryRepository.count();
        
        // Read a category
        Category category = categoryRepository.findOne(1L);

        // Get the category
        restCategoryMockMvc.perform(delete("/api/categories/{id}", category.getId())
                .accept(ResourceTestUtils.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database has one less item
        List<Category> categories = categoryRepository.findAll();
        assertThat(categories).hasSize(count - 1);
    }
}
