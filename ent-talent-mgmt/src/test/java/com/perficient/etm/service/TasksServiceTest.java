package com.perficient.etm.service;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.perficient.etm.exception.ReviewProcessNotFound;
import com.perficient.etm.service.activiti.ProcessService;
import com.perficient.etm.service.activiti.TasksService;
import com.perficient.etm.utils.SpringAppTest;

public class TasksServiceTest extends SpringAppTest{

	@Autowired
	private TasksService taskSvc;
	
	@Autowired
	private ProcessService processSvc;
	
	private String processsIdStarted;
	
	//User 1 = Reviwer
	//User 2 = Reviewee
	@Before
	public void init() throws ReviewProcessNotFound{
		processsIdStarted = ServicesTestUtils.startAnnualReviewProcess(processSvc); 
	}
	
	
	@Test
	public void testToDoList(){
		List<String> tasks = taskSvc.getTasks("2");
		assertEquals("At the beginign one task has to be assigned to the reviewee",tasks.size(), 1);
		
		tasks = taskSvc.getTasks("1");
		
		assertEquals("At the beginign 0 tasks has to be assigned to the reviewer",tasks.size(), 0);
		
	}
	
	@Test
	public void testRetrieveOneTask(){
		//Retrieve the todo list
		List<String> tasks = taskSvc.getTasks("2");
		
		String taskId = taskSvc.getTask(tasks.get(0));
		
		assertEquals("The tasks retrieved should be the same",tasks.get(0), taskId);
	}
	
	@Test
	public void testCompleteTask(){
		List<String> tasks = taskSvc.getTasks("2");
		
		taskSvc.complete(tasks.get(0), "COMPLETE");
		
	}
	
	
	@Test
	public void testRetrieveProcessTasks(){
		List<String> ids = taskSvc.getProcessTasks(processsIdStarted);
		assertNotNull(ids);
	}
	
}
