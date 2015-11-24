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

import com.perficient.etm.utils.SpringAppTest;

public class ReviewReminderWorkflowTest extends SpringAppTest {
	
	@Autowired
	private RuntimeService runtimeService;	
	@Autowired
	private TaskService taskService;
	
	
	
//	@Before
//	public void setup() {
//		ProcessEngine processEngine = ProcessEngineConfiguration
//				.createStandaloneInMemProcessEngineConfiguration()
//				.buildProcessEngine();
//
//		RepositoryService repositoryService = processEngine
//				.getRepositoryService();
//		repositoryService.createDeployment()
//		.addClasspathResource("processes/create_review_reminder.bpmn").deploy();	
//		runtimeService = processEngine.getRuntimeService();
//		identityService = processEngine.getIdentityService();
//		taskService = processEngine.getTaskService();
//		
//	}
		
	@Test
	public void testHappyPath() {
		
		Map<String, Object> variables = new HashMap<>();
		variables.put("Reviewee", "Alex");
		variables.put("RevieweeEmail", "alex@perficient.com");
		
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
				"CreateReviewReminder", variables);
		assertNotNull(processInstance.getId());
		
		Task t = 
		taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
		
		assertNotNull(t);
		taskService.complete(t.getId());
		 
		
	}
	
	@Test
	public void testEmailTrigger(){
			
	}
	
}
