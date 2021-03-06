package com.perficient.etm.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Before;
import org.junit.Test;

public class BasicWorkflowTest {

    private RuntimeService runtimeService;

    private TaskService taskService;

    @Before
    public void setup() {
        ProcessEngine processEngine = ProcessEngineConfiguration
                .createStandaloneInMemProcessEngineConfiguration()
                .buildProcessEngine();

        RepositoryService repositoryService = processEngine
                .getRepositoryService();
        repositoryService.createDeployment()
        .addClasspathResource("processes/annualReview.bpmn20.xml").deploy();
        runtimeService = processEngine.getRuntimeService();
        taskService = processEngine.getTaskService();

    }

    @Test
    public void testSuccessfulCompletion() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                "annualReviewTest");
        assertNotNull(processInstance.getId());
        System.out.println("id " + processInstance.getId() + ", definition id "
                + processInstance.getProcessDefinitionId());

        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionId(processInstance.getProcessDefinitionId())
                .taskName("employeeFeedback")
                .list();
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        Task employeeFeedback = tasks.get(0);
        employeeFeedback.setAssignee("Craig Smith");
        taskService.complete(employeeFeedback.getId());

        tasks = taskService.createTaskQuery()
                .processDefinitionId(processInstance.getProcessDefinitionId())
                .taskName("counselorFeedback")
                .list();
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        Task counselorFeedback = tasks.get(0);
        employeeFeedback.setAssignee("David Brooks");
        taskService.complete(counselorFeedback.getId());

        tasks = taskService.createTaskQuery()
                .processDefinitionId(processInstance.getProcessDefinitionId())
                .taskName("mgmtReview")
                .list();
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        Task mgmtReview = tasks.get(0);
        employeeFeedback.setAssignee("Art Quinn");
        taskService.complete(mgmtReview.getId());
    }
}
