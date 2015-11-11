package com.perficient.etm.workflow;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.*;

import com.perficient.etm.utils.SpringAppTest;

public class AnnualReviewTest extends SpringAppTest {
	@Autowired private RuntimeService runtimeSvc;
	@Autowired private TaskService taskSvc;
	
	@Test
    public void shouldCompleteReview() {
		ProcessInstance processInstance = runtimeSvc.startProcessInstanceByKey("annualReview");
		assertNotNull(processInstance);
		System.out.println(processInstance.getActivityId());
		
	}
}
