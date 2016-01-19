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
import com.perficient.etm.domain.Todo;
import com.perficient.etm.domain.User;
import com.perficient.etm.exception.ETMException;
import com.perficient.etm.service.ServicesTestUtils;
import com.perficient.etm.utils.SpringAppTest;

public class TasksServiceTest extends SpringAppTest {

    @Autowired
    private TasksService taskSvc;

    @Autowired
    private ProcessService processSvc;

    private String processsIdStarted;

    //User 1 = Reviwer
    //User 2 = Reviewee
    @Before
    public void init() throws ETMException {
        processsIdStarted = ServicesTestUtils.startAnnualReviewProcess(processSvc);
    }

    @Test
    public void testTodoList() {
        List<Task> tasks = taskSvc.getTasks(2L);
        assertEquals("At the beginign one task has to be assigned to the reviewee",tasks.size(), 1);
        tasks = taskSvc.getTasks(1L);
        assertEquals("At the beginign 0 tasks has to be assigned to the reviewer",tasks.size(), 0);
    }

    @Test
    public void testTodoListUsingTodoObjects() {
        User u = Mockito.mock(User.class);

        List<Task> tasks = taskSvc.getTasks(2L);
        //Convert them to Todo objects
        List<Todo> todos = tasks.stream().map(t->Todo.fromTask(t , u)).collect(Collectors.toList());
        assertEquals("Todos and Tasks lists should have the same number of elements",todos.size(), tasks.size());
        Todo t = todos.get(0);
        //Assert the content of the todo lists
        assertNotNull("Task id from activiti should be populated in the Todo object", t.getId());
        assertNotNull("User should be populated in the Todo object", t.getUserId());
        assertNotNull("Task name from activiti should be populated in the Todo object", t.getName());
    }

    @Test
    public void testCompleteTask() {
        List<Task> tasks = taskSvc.getTasks(2L);

        taskSvc.complete(tasks.get(0).getId(), "COMPLETE");

    }

    @Test
    public void testRetrieveProcessTasks() {
        Task task = taskSvc.getProcessTask(processsIdStarted, 2L);
        assertNotNull(task);
    }
}
