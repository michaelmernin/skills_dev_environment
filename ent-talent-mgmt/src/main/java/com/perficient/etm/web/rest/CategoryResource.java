package com.perficient.etm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.perficient.etm.domain.Category;
import com.perficient.etm.exception.ResourceNotFoundException;
import com.perficient.etm.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Category.
 */
@RestController
@RequestMapping("/api")
public class CategoryResource {

    private final Logger log = LoggerFactory.getLogger(CategoryResource.class);

    @Inject
    private CategoryRepository categoryRepository;

    /**
     * POST  /categories -> Create a new category.
     */
    @RequestMapping(value = "/categories",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody Category category) {
        log.debug("REST request to save Category : {}", category);
        categoryRepository.save(category);
    }

    /**
     * GET  /categories -> get all the categories.
     */
    @RequestMapping(value = "/categories",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Category> getAll() {
        log.debug("REST request to get all Categories");
        return categoryRepository.findAll();
    }

    /**
     * GET  /categories/:id -> get the "id" category.
     */
    @RequestMapping(value = "/categories/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Category> get(@PathVariable Long id) {
        log.debug("REST request to get Category : {}", id);
        return Optional.ofNullable(categoryRepository.findOne(id))
            .map(category -> new ResponseEntity<>(
                category,
                HttpStatus.OK))
            .orElseThrow(() -> {
                return new ResourceNotFoundException("Category " + id + " cannot be found.");
            });
    }
}
