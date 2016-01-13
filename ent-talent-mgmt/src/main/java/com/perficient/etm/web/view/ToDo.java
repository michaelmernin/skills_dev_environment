package com.perficient.etm.web.view;

import org.activiti.engine.task.Task;
import org.joda.time.LocalDate;

import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.User;

/**
 * The view representation for a Task in activiti engine.
 * This object will be returned to the front-end application
 * in order to render tasks for the users.
 * @author Alexandro Blanco <alex.blanco@perficient.com>
 *
 */
public class ToDo {

    private User user;

    private LocalDate dueDate;

    private String name;

    private String description;

    private String activitiTaskId;

    private Review review;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getActivitiTaskId() {
        return activitiTaskId;
    }

    public void setActivitiTaskId(String activitiTaskId) {
        this.activitiTaskId = activitiTaskId;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
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
        ToDo t = new ToDo();
        t.setName(task.getName());
        t.setActivitiTaskId(task.getId());
        t.setUser(user);
        return t;
    }
}
