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
import com.perficient.etm.domain.Feedback;
import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.Todo;
import com.perficient.etm.domain.User;
import com.perficient.etm.repository.FeedbackRepository;
import com.perficient.etm.repository.ReviewRepository;
import com.perficient.etm.service.activiti.ProcessConstants;
import com.perficient.etm.service.activiti.TasksService;
import com.perficient.etm.web.rest.dto.TodoActionDTO;

@Service
public class TodoService extends AbstractBaseService {
    
    @Inject
    private TasksService tasksService;
    
    @Inject
    private UserService userService;
    
    @Inject
    private ReviewRepository reviewRepository;
    
    @Inject
    private FeedbackRepository feedbackRepository;

    public Optional<Todo> findOneActiveByReviewForCurrentUser(Long reviewId) {
        return userService.getUserFromLogin().map(user -> {
            return getReviewActiveTodo(reviewId, user);
        });
    }

    public List<Todo> findActiveForCurrentUser() {
        return userService.getUserFromLogin().map(this::getUserTodos)
            .orElse(Collections.emptyList());
    }
    
    public void complete(TodoActionDTO action) {
        Optional.ofNullable(tasksService.getTask(action.getTodoId())).ifPresent(task -> {
            String processVariable;
            switch (task.getTaskDefinitionKey()) {
            case "ReviewerJointApproval":
            case "ReviewerOneJointApproval":
            case "ReviewerTwoJointApproval":
                processVariable = ProcessConstants.REVIEWER_RESULT_VARIABLE;
                break;
            case "RevieweeJointApproval":
            case "RevieweeOneJointApproval":
            case "RevieweeTwoJointApproval":
                processVariable = ProcessConstants.REVIEWEE_RESULT_VARIABLE;
                break;
            case "ManagerTwoJointApproval":
                processVariable = ProcessConstants.MANAGER_RESULT_VARIABLE;
                break;
            default:
                processVariable = ProcessConstants.RESULT_VARIABLE;
                break;
            }
            tasksService.complete(action.getTodoId(), action.getResult(), processVariable);
        });        
    }
    
    private Todo getReviewActiveTodo(Long reviewId, User user) {
        return Optional.ofNullable(reviewRepository.findOne(reviewId))
            .map(findProcessId(user))
            .map(processId -> {
                return tasksService.getProcessUserTask(processId, user.getId());
            })
            .map(mapTask(user))
            .orElse(null);
    }
    
    private List<Todo> getUserTodos(User user) {
        return tasksService.getUserTasks(user.getId())
            .stream().map(mapTask(user))
            .collect(Collectors.toList());
    }
    
    private Function<? super Review, ? extends String> findProcessId(User user) {
        return review -> {
          if (ReviewAuthorizer.isPeer(review, user.getLogin())) {
              return feedbackRepository.findOneByReviewIdAndAuthorId(review.getId(), user.getId())
                  .map(Feedback::getProcessId)
                  .orElse(null);
          } else {
              return review.getProcessId();
          }
        };
    }

    private Function<? super Task, ? extends Todo> mapTask(User user) {
        return task -> {
            Long reviewId = (long)task.getProcessVariables().get(ProcessConstants.REVIEW_VARIABLE);
            Review review = reviewRepository.findOne(reviewId);
            return Todo.fromTask(task, user, review);
        };
    }
}
