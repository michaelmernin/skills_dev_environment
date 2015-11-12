package com.perficient.etm.workflow;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.perficient.etm.utils.SpringAppTest;
import static java.lang.System.out;


public class AnnualReviewTest extends SpringAppTest {
	@Autowired private RuntimeService runtimeSvc;
	@Autowired private TaskService taskSvc;
	
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
	
}
