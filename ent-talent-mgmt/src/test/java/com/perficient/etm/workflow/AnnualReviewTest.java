package com.perficient.etm.workflow;

import static java.lang.System.out;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.ProcessInstanceHistoryLog;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.perficient.etm.utils.SpringAppTest;


public class AnnualReviewTest extends SpringAppTest {
	@Autowired private RuntimeService runtimeSvc;
	@Autowired private TaskService taskSvc;
	@Autowired private HistoryService historySvc;
	
	@Test
    public void shouldCompleteReview() {
		ProcessInstance processInstance = runtimeSvc.startProcessInstanceByKey("annualReviewTest");
		assertNotNull(processInstance);
		System.out.println(processInstance.getActivityId());
		
	}
	
	private Map<String, Object> getProcessVariables(){
		Map<String,Object> variables = new HashMap<>();
		variables.put("Reviewee", "Alex");
		variables.put("Reviewer", "Craig");
		variables.put("Director", "David");
		return variables;
	}
	
	@Test
	public void testAnnualReview(){
		
		Map<String,Object> variables = getProcessVariables();
		ProcessInstance processInstance = runtimeSvc.startProcessInstanceByKey("annualReview",variables);
		assertNotNull(processInstance);
		System.out.println(processInstance.getActivityId());
		
		List<Task> list = taskSvc.createTaskQuery().taskAssignee("Alex").list();
		List<Task> list2 = taskSvc.createTaskQuery().taskAssignee("Craig").list();
		
		out.println(list);
		
		assert(list.size() == 1);
		assert(list2.size() == 0);
		
		//Complete the first one and check for the task of the reviewer
		taskSvc.complete(list.get(0).getId());
		
		list = taskSvc.createTaskQuery().taskAssignee("Alex").list();
		list2 = taskSvc.createTaskQuery().taskAssignee("Craig").list();
		
		assert(list.size() == 0);
		assert(list2.size() == 1);
		
	}
	
	
	
	
	@Test
	public void testCancel(){
		Map<String,Object> variables = getProcessVariables();
		ProcessInstance processInstance = runtimeSvc.startProcessInstanceByKey("annualReview",variables);
		assertNotNull(processInstance);
		
		runtimeSvc.deleteProcessInstance(processInstance.getProcessInstanceId(), "Cancel Process");
	}
	
	@Test(expected = ActivitiObjectNotFoundException.class)
	public void testdeleteProcessNotFound(){
		runtimeSvc.deleteProcessInstance("engagementReview:123", "Test Delete");
	}
	
	@Test
	public void testHappyPath(){
		Map<String,Object> variables = getProcessVariables();
		ProcessInstance processInstance = runtimeSvc.startProcessInstanceByKey("annualReview",variables);
		
		//Self review
		Task t = completeAndGetNextTask(null,processInstance.getId());
		assertNotNullAndAsignee(t,"Alex");
		
		//Reviewers review
		t = completeAndGetNextTask(t,processInstance.getId());
		assertNotNullAndAsignee(t,"Craig");
		
		//Directors review
		t = completeAndGetNextTask(t,processInstance.getId());
		assertNotNullAndAsignee(t,"David");
		
		//Joint review
		t = completeAndGetNextTask(t,processInstance.getId());
		assertNotNullAndAsignee(t,"Craig");
		
		//Final review
		t = completeAndGetNextTask(t,processInstance.getId());
		assertNotNullAndAsignee(t,"Craig");
		
		//Process should be done by now
		t = completeAndGetNextTask(t,processInstance.getId());
		assertNull(t);
		//Save the process Instance Id
		String processInstanceId = processInstance.getId();
		//check this if historical data is enabled and check for isEnded() method. might be different
		processInstance = runtimeSvc.createProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult();
		assertNull(processInstance);
		
		ProcessInstanceHistoryLog result = historySvc.createProcessInstanceHistoryLogQuery(processInstanceId).singleResult();
		assertNotNull(result);
		assertNotNull("There should be an end time for the finished process",result.getEndTime());
	}
	
	@Test
	public void testReviewersReject(){
		Map<String,Object> variables = getProcessVariables();
		ProcessInstance processInstance = runtimeSvc.startProcessInstanceByKey("annualReview",variables);
		
		//Self review
		Task t = completeAndGetNextTask(null,processInstance.getId());
		assertNotNullAndAsignee(t,"Alex");
		
		//Reviewers review
		t = completeAndGetNextTask(t, processInstance.getId());
		assertNotNullAndAsignee(t,"Craig");
		//Fail the reviewers review
		t = completeWithAndGetNextTask(t, "FAILURE", processInstance.getId());
		
		//flow has to go back to initial reviewer
		assertNotNullAndAsignee(t,"Alex");
		
	}
	
	/**
	 * Completes the current task with RESULT variable as SUCCESS
	 * by calling the TaskService.complete method
	 * @param t The Task object to complete
	 */
	private void completeATaskWithSucess(Task t){
		completeATaskWith(t, "SUCCESS");
	}
	
	/**
	 * Completes the current task with RESULT variable as specified
	 * in the parameters and by calling the TaskService.complete method
	 * @param t The Task object to complete
	 * @param result The String result to set in the complete variables
	 */
	private void completeATaskWith(Task t, String result){
		Map<String, Object> properties = new HashMap<>();
		properties.put("RESULT", result);
		taskSvc.complete(t.getId(), properties);
	}
	
	/**
	 * Completes the current Task t if the task is not null and retrieves the 
	 * task where the process with processInstanceId landed. The task will
	 * be completed with a RESULT = SUCCESS
	 * @param t The Task to complete if necessary
	 * @param processInstanceId The String process Instance id of the process
	 * @return Task object if the process landed on a next task. Null if the process
	 * ended
	 */
	private Task completeAndGetNextTask(Task t, String processInstanceId){
		return completeWithAndGetNextTask(t,"SUCCESS", processInstanceId);
	}
	
	/**
	 * Completes the current Task t if the task is not null and retrieves the 
	 * task where the process with processInstanceId landed. The task will
	 * be completed with a RESULT = result param
	 * @param t The Task to complete if necessary
	 * @param result The String result to complete the task with
	 * @param processInstanceId The String process Instance id of the process
	 * @return Task object if the process landed on a next task. Null if the process
	 * ended
	 */
	private Task completeWithAndGetNextTask(Task t, String result, String processInstanceId){
		if (t != null)
			completeATaskWith(t,result);
		
		//Reviewers Review
		t = taskSvc.createTaskQuery().processInstanceId(processInstanceId).singleResult();
		return t;
	}
	
	/**
	 * invokes assertNotNull in the passed task and assertEquals in Task.getAssignee() with the
	 * assignee parameter
	 * @param t The Task to assert
	 * @param assignee The String value of assignee
	 */
	private void assertNotNullAndAsignee(Task t, String assignee){
		assertNotNull(t);
		assertEquals("Asignee for task "+t.getName() + " is not "+assignee+". Value = "+t.getAssignee()
			,assignee, t.getAssignee());
	}
	
}
