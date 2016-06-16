package com.perficient.etm.service.activiti;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.stream.Collectors;

import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;

import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.Todo;
import com.perficient.etm.domain.TodoResult;
import com.perficient.etm.domain.User;
import com.perficient.etm.exception.ETMException;
import com.perficient.etm.service.ServicesTestUtils;
import com.perficient.etm.utils.SpringAppTest;
@WithUserDetails("dev.user3")
public class TasksServiceTest extends SpringAppTest {

    @Autowired
    private TasksService taskSvc;

    @Autowired
    private ProcessService processSvc;

    private String processsIdStarted;
    
    long REVIEWEE_ID = 6L;
    long REVIEWER_ID = 5L;
    String REVIEWEE_LOGIN = "dev.user4";
    String REVIEWER_LOGIN = "dev.user3";
    
    private int revieweeTasks;
    private int reviewerTasks;
    

    //User 1 = Reviwer - 5
    //User 2 = Reviewee - 6
    @Before
    @WithUserDetails("dev.user3")
    public void init() throws ETMException {
    	revieweeTasks = taskSvc.getUserTasks(REVIEWEE_ID).size();
    	reviewerTasks = taskSvc.getUserTasks(REVIEWER_ID).size();
        processsIdStarted = ServicesTestUtils.startAnnualReviewProcess(processSvc);
    }

    @Test
    public void testTodoList() {
        int NewRevieweeTasks = taskSvc.getUserTasks(REVIEWEE_ID).size();
        assertEquals("At the beginign"+ revieweeTasks+1 +" task has to be assigned to the reviewee",revieweeTasks +1, NewRevieweeTasks);
        int NewReviewerTasks = taskSvc.getUserTasks(REVIEWER_ID).size();
        assertEquals("At the beginign the same tasks () have to be assigned to the reviewer",reviewerTasks, NewReviewerTasks);
    }

    @Test
    public void testTodoListUsingTodoObjects() {
        User u = Mockito.mock(User.class);

        List<Task> tasks = taskSvc.getUserTasks(REVIEWEE_ID);
        //Convert them to Todo objects
        List<Todo> todos = tasks.stream().map(t->Todo.fromTask(t , u, new Review())).collect(Collectors.toList());
        assertEquals("Todos and Tasks lists should have the same number of elements",todos.size(), tasks.size());
        Todo t = todos.get(todos.size()-1);
        //Assert the content of the todo lists
        assertNotNull("Task id from activiti should be populated in the Todo object", t.getId());
        assertNotNull("User should be populated in the Todo object", t.getUserId());
        assertNotNull("Task name from activiti should be populated in the Todo object", t.getName());
    }

    @Test
    @WithUserDetails("dev.user3")
    public void testCompleteTask() {
        List<Task> tasks = taskSvc.getUserTasks(REVIEWEE_ID);
        taskSvc.complete(tasks.get(tasks.size() - 1).getId(), TodoResult.SUBMIT);
    }

    @Test
    public void testRetrieveProcessTasks() {
        Task task = taskSvc.getProcessUserTask(processsIdStarted, REVIEWEE_ID);
        assertNotNull(task);
    }
}
