package com.perficient.etm.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.test.context.support.WithUserDetails;

import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.User;
import com.perficient.etm.exception.ActivitiProcessInitiationException;
import com.perficient.etm.exception.ETMException;
import com.perficient.etm.exception.ReviewProcessNotFound;
import com.perficient.etm.repository.ReviewTypeRepository;
import com.perficient.etm.service.activiti.ProcessService;
import com.perficient.etm.service.activiti.TasksService;
import com.perficient.etm.utils.SpringAppTest;
import com.perficient.etm.web.view.ToDo;
/**
 * JUnit for the ReviewService class
 * @author Alexandro Blanco <alex.blanco@perficient.com>
 *
 */
@WithUserDetails("dev.user2")
public class ReviewServiceTest extends SpringAppTest {

	@Inject
	ReviewService reviewSvc;
	@Inject
	ReviewTypeRepository reviewTypRepo;
	
	
	@Mock
	ProcessService processSvc;
	
	@Mock
	ProcessService processSvcWithException;
	
	@Mock
	TasksService taskSvc;
	
	Review review;
	
	@Before
	public void init() throws ETMException{
		MockitoAnnotations.initMocks(this);
		review = ServicesTestUtils.createNewReviewForTests(reviewTypRepo);
		
		Mockito.when(processSvc.initiateProcess(Mockito.any(), Mockito.any())).thenReturn("ProcessId[5]");
		Mockito.when(processSvcWithException.initiateProcess(Mockito.any(), Mockito.any())).thenThrow(new RuntimeException("Test"));
		
		List<org.activiti.engine.task.Task> mockTasksList =  new ArrayList<>();
		
		Mockito.when(taskSvc.getTasks(Mockito.anyString())).thenReturn(
					mockTasksList
				);
		
		reviewSvc.setProcessSvc(processSvc);
		reviewSvc.setTasksService(taskSvc);
	}
	
	@Test
	public void testStartReviewProcess() throws ETMException{
		//List<Review> revs = reviewSvc.findAll();
		reviewSvc.startReviewProcess(review);
		//List<Review> revsAfter = reviewSvc.findAll();
		assertNotNull("A New Review object should have been created in db and it's id should be set in ReviewProcessId", 
				review.getReviewProcessId());
	}
	
	@Test(expected=ActivitiProcessInitiationException.class)
	public void testExceptionDuringInitiation() throws ETMException{
		reviewSvc.setProcessSvc(processSvcWithException);
		reviewSvc.startReviewProcess(review);
		assertNull("No process id should be set", review.getReviewProcessId());
	}
	
	@Test(expected=ReviewProcessNotFound.class)
	public void testStartProcessWithReviewNoType() throws ETMException{
		review.setReviewType(null);
		reviewSvc.startReviewProcess(review);
	}
	
	@Test
	public void testUpdate(){
		reviewSvc.update(review);
		
	}
	
	@Test
	public void testFindAll(){
		List<Review> list = reviewSvc.findAll();
		assertNotNull(list);
	}
	
	@Test
	public void testTodoList(){
		List<ToDo> tasks = reviewSvc.getUsersReviewTodo(new User());
		assertNotNull(tasks);
	}
	
	
	
}
