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
public class ToDo implements Serializable {

    private static final long serialVersionUID = 1033412703245531184L;

    private Long userId;

    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    private LocalDate dueDate;

    private String name;

    private String description;

    private String taskId;

    private Long reviewId;

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

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    /**
     * Builds a new ToDo object based on the Activiti Task object and the
     * designated user
     * @param task the Task object that will serve as based for the
     * new object
     * @param user the User object used that this task belongs to
     * @return ToDo object with the information from the Task
     */
    public static ToDo fromTask(Task task, User user) {
        ToDo todo = new ToDo();
        todo.setName(task.getName());
        todo.setTaskId(task.getId());
        todo.setUserId(user.getId());
        Optional.ofNullable(task.getProcessVariables()).ifPresent(vars -> {
            todo.setReviewId((Long) vars.get(ProcessConstants.REVIEW_VARIABLE));
        });
        return todo;
    }
}
