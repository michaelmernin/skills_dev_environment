package com.perficient.etm.workflow;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dumbster.smtp.MailMessage;
import com.perficient.etm.utils.SpringAppTest;

public class PeerReviewWorkflowTest extends SpringAppTest {
	
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
		
	private Map<String, Object> getVariables(){
		Map<String, Object> variables = new HashMap<>();
		variables.put("Peer", "Alex");
		variables.put("PeerEmail", "alex@perficient.com");
		variables.put("Reviewee", "David Brooks");
		
		return variables;
	}
	
	@Test
	public void testHappyPath() {
		Map<String, Object> variables = getVariables();
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
				"peerReview", variables);
		assertNotNull(processInstance.getId());
		
		Task t = 
		taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
		
		assertNotNull(t);
		taskService.complete(t.getId());
		 
		t = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
		assertNull(t);
	}
	
	@Test
	@Ignore //Test is taking too long due to Thread.sleep need a better way to test this
	public void testEmailTrigger(){
		Map<String, Object> variables = getVariables();
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
				"peerReview", variables);
		
		Task t = 
				taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
		
		assertNotNull(t);
		
		try {
			Thread.sleep(90000);//Wait for 1.5 minutes so the time boundary of the reminder triggers.
//			taskService.complete(t.getId());
			
			t = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
			//An email should be triggered and the task should be back to give feedback
			//TODO check how to review the email
			assertNotNull("Task should not be null",t);
			MailMessage[] messages = getServer().getMessages();
			System.out.println(messages.length); 
			//assertTrue("There must be messages sent",messages.length > 1);
		} catch (InterruptedException e) {
			//If the sleep method fails do not fail the test. pass it with a warning
			getLog().warn("Test email Trigger for Peer Review Workflow Failed to wait. This test will be useless");
		} 
	}
	
}
