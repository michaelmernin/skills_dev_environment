package com.perficient.etm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.perficient.etm.domain.Todo;
import com.perficient.etm.exception.InvalidRequestException;
import com.perficient.etm.service.TodoService;
import com.perficient.etm.web.rest.dto.TodoActionDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import java.util.List;

/**
 * REST controller for managing Todo items.
 */
@RestController
@RequestMapping("/api")
public class TodoResource implements RestResource {

    private final Logger log = LoggerFactory.getLogger(TodoResource.class);

    @Inject
    private TodoService todoService;
    
    /**
     * GET /reviews/:id/todo -> get the todo for a review.
     */
    @RequestMapping(value = "/reviews/{reviewId}/todo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Todo> getActiveByReview(@PathVariable Long reviewId) {
        log.debug("REST request to get active todo for Review : {}", reviewId);
        return todoService.findOneActiveByReviewForCurrentUser(reviewId)
                .map(todo -> new ResponseEntity<>(todo, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.NO_CONTENT));
    }

    /**
     * GET /todos -> get the todo for a review.
     */
    @RequestMapping(value = "/todo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Todo> getAll() {
        log.debug("REST request to get all todos for current user");
        return todoService.findActiveForCurrentUser();
    }
    
    /**
     * PUT /todos/:id -> complete the todo
     */
    @RequestMapping(value = "/todos/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@PathVariable String id, @Valid @RequestBody TodoActionDTO action,
            BindingResult result) {
        log.debug("REST request to complete todo : {}", id);
        if (result.hasErrors()) {
            throw new InvalidRequestException("Invalid todo action", result);
        }
        todoService.complete(action);
        return ResponseEntity.ok().build();
    }
}
