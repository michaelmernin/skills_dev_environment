package com.perficient.etm.domain;

import java.io.Serializable;
import java.util.Optional;

import org.activiti.engine.task.Task;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.perficient.etm.domain.util.CustomLocalDateSerializer;
import com.perficient.etm.domain.util.ISO8601LocalDateDeserializer;
import com.perficient.etm.service.activiti.ProcessConstants;

/**
 * The view representation for a Task in activiti engine.
 * This object will be returned to the front-end application
 * in order to render tasks for the users.
 * @author Alexandro Blanco <alex.blanco@perficient.com>
 *
 */
public class Todo implements Serializable {

    private static final long serialVersionUID = -8220058339083524517L;

    private String id;
    
    private Long userId;

    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    private LocalDate dueDate;

    private String name;

    private String description;

    private Review review;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    /**
     * Builds a new Todo object based on the Activiti Task object and the
     * designated user
     * @param task the Task object that will serve as based for the
     * new object
     * @param user the User object used that this task belongs to
     * @return Todo object with the information from the Task
     */
    public static Todo fromTask(Task task, User user, Review review) {
        Todo todo = new Todo();
        todo.setName(task.getName());
        todo.setId(task.getId());
        todo.setUserId(user.getId());
        todo.setReview(review);
        /*Optional.ofNullable(task.getProcessVariables()).ifPresent(vars -> {
            todo.setReviewId((Long) vars.get(ProcessConstants.REVIEW_VARIABLE));
        });*/
        return todo;
    }
}
