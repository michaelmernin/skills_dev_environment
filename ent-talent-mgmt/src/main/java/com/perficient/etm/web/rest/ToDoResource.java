package com.perficient.etm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.perficient.etm.domain.ToDo;
import com.perficient.etm.exception.ResourceNotFoundException;
import com.perficient.etm.service.ToDoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

/**
 * REST controller for managing ToDo items.
 */
@RestController
@RequestMapping("/api")
public class ToDoResource implements RestResource {

    private final Logger log = LoggerFactory.getLogger(ToDoResource.class);

    @Inject
    private ToDoService toDoService;
    
    /**
     * GET /reviews/:id/todo -> get the todo for a review.
     */
    @RequestMapping(value = "/reviews/{reviewId}/todo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ToDo> getActiveByReview(@PathVariable Long reviewId) {
        log.debug("REST request to get active todo for Review : {}", reviewId);
        return toDoService.findOneActiveByReviewForCurrentUser(reviewId)
                .map(todo -> new ResponseEntity<>(todo, HttpStatus.OK))
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Active ToDo for Review " + reviewId + " cannot be found.");
                });
    }

    /**
     * GET /todos -> get the todo for a review.
     */
    @RequestMapping(value = "/todo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ToDo> getAll() {
        log.debug("REST request to get all todos for current user");
        return toDoService.findActiveForCurrentUser();
    }
}
