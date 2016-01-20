package com.perficient.etm.workflow;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.perficient.etm.service.activiti.ProcessConstants;
import com.perficient.etm.utils.SpringAppTest;

public class ReviewReminderWorkflowTest extends SpringAppTest {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Test
    public void testHappyPath() {
        Map<String, Object> variables = new HashMap<>();
        variables.put(ProcessConstants.REVIEWEE_VARIABLE, "Alex");
        variables.put(ProcessConstants.REVIEWEE_EMAIL_VARIABLE, "alex@perficient.com");

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                "CreateReviewReminder", variables);
        assertNotNull(processInstance.getId());

        Task t =
        taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

        assertNotNull(t);
        taskService.complete(t.getId());
    }

    @Test
    public void testEmailTrigger() {

    }
}
