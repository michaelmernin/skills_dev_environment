package com.perficient.etm.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;

import com.perficient.etm.authorize.ReviewAuthorizer;
import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.ToDo;
import com.perficient.etm.domain.User;
import com.perficient.etm.repository.FeedbackRepository;
import com.perficient.etm.repository.ReviewRepository;
import com.perficient.etm.service.activiti.TasksService;

@Service
public class ToDoService extends AbstractBaseService {
    
    @Inject
    private TasksService tasksService;
    
    @Inject
    private UserService userService;
    
    @Inject
    private ReviewRepository reviewRepository;
    
    @Inject
    private FeedbackRepository feedbackRepository;

    public Optional<ToDo> findOneActiveByReviewForCurrentUser(Long reviewId) {
        return userService.getUserFromLogin().map(user -> {
            return getReviewActiveTodo(reviewId, user);
        });
    }

    public List<ToDo> findActiveForCurrentUser() {
        return userService.getUserFromLogin().map(this::getUserTodos)
            .orElse(Collections.emptyList());
    }
    
    private ToDo getReviewActiveTodo(Long reviewId, User user) {
        return Optional.ofNullable(reviewRepository.findOne(reviewId))
            .map(findProcessId(user))
            .map(processId -> {
                return tasksService.getProcessTask(processId, user.getId());
            })
            .map(mapTask(user))
            .orElse(null);
    }
    
    private List<ToDo> getUserTodos(User user) {
        return tasksService.getTasks(user.getId())
            .stream().map(mapTask(user))
            .collect(Collectors.toList());
    }
    
    private Function<? super Review, ? extends String> findProcessId(User user) {
        return review -> {
          if (ReviewAuthorizer.isPeer(review, user.getLogin())) {
              return Optional.ofNullable(feedbackRepository.findOneByReviewAndAuthor(review, user))
                  .map(feedback -> {
                      return feedback.getProcessId();
                  }).orElse(null);
          } else {
              return review.getProcessId();
          }
        };
    }

    private Function<? super Task, ? extends ToDo> mapTask(User user) {
        return task -> {
            return ToDo.fromTask(task, user);
        };
    }
}
