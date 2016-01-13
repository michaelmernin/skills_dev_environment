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

import com.perficient.etm.domain.User;
import com.perficient.etm.exception.ETMException;
import com.perficient.etm.service.ServicesTestUtils;
import com.perficient.etm.utils.SpringAppTest;
import com.perficient.etm.web.view.ToDo;

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
    public void testToDoList() {
        List<Task> tasks = taskSvc.getTasks("2");
        assertEquals("At the beginign one task has to be assigned to the reviewee",tasks.size(), 1);
        tasks = taskSvc.getTasks("1");
        assertEquals("At the beginign 0 tasks has to be assigned to the reviewer",tasks.size(), 0);
    }

    @Test
    public void testToDoListUsingToDoObjects() {
        User u = Mockito.mock(User.class);

        List<Task> tasks = taskSvc.getTasks("2");
        //Convert them to ToDo objects
        List<ToDo> todos = tasks.stream().map(t->ToDo.fromTask(t , u)).collect(Collectors.toList());
        assertEquals("Todos and Tasks lists should have the same number of elements",todos.size(), tasks.size());
        ToDo t = todos.get(0);
        //Assert the content of the todo lists
        assertNotNull("Task id from activiti should be populated in the ToDo object", t.getActivitiTaskId());
        assertNotNull("User should be populated in the ToDo object", t.getUser());
        assertNotNull("Task name from activiti should be populated in the ToDo object", t.getName());
    }

    @Test
    public void testRetrieveOneTask() {
        //Retrieve the todo list
        List<Task> tasks = taskSvc.getTasks("2");

        String taskId = taskSvc.getTask(tasks.get(0).getId());

        assertEquals("The tasks retrieved should be the same",tasks.get(0).getId(), taskId);
    }

    @Test
    public void testCompleteTask() {
        List<Task> tasks = taskSvc.getTasks("2");

        taskSvc.complete(tasks.get(0).getId(), "COMPLETE");

    }

    @Test
    public void testRetrieveProcessTasks() {
        List<String> ids = taskSvc.getProcessTasks(processsIdStarted);
        assertNotNull(ids);
    }
}
