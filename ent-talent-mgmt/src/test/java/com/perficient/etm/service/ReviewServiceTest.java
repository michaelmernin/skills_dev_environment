package com.perficient.etm.service;

import static org.junit.Assert.*;

import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.test.context.support.WithUserDetails;

import com.perficient.etm.domain.Review;
import com.perficient.etm.exception.ReviewProcessNotFound;
import com.perficient.etm.repository.ReviewTypeRepository;
import com.perficient.etm.service.activiti.ProcessService;
import com.perficient.etm.utils.SpringAppTest;
/**
 * JUnit for the ReviewService class
 * @author Alexandro Blanco <alex.blanco@perficient.com>
 *
 */
public class ReviewServiceTest extends SpringAppTest {

	@Inject
	ReviewService reviewSvc;
	@Inject
	ReviewTypeRepository reviewTypRepo;
	
	
	@Mock
	ProcessService processSvc;
	
	Review review;
	
	@Before
	public void init() throws ReviewProcessNotFound{
		MockitoAnnotations.initMocks(this);
		review = ServicesTestUtils.createNewReviewForTests(reviewTypRepo);
		
		Mockito.when(processSvc.initiateProcess(Mockito.any(), Mockito.any())).thenReturn("ProcessId[5]");
		reviewSvc.setProcessSvc(processSvc);
	}
	
	@Test
	@WithUserDetails("dev.user2")
	public void testStartReviewProcess() throws ReviewProcessNotFound{
		//List<Review> revs = reviewSvc.findAll();
		reviewSvc.startReviewProcess(review);
		//List<Review> revsAfter = reviewSvc.findAll();
		assertNotNull("A New Review object should have been created in db and it's id should be set in ReviewProcessId", 
				review.getReviewProcessId());
	}
	
	@Test(expected=ReviewProcessNotFound.class)
	public void testStartProcessWithReviewNoType() throws ReviewProcessNotFound{
		review.setReviewType(null);
		reviewSvc.startReviewProcess(review);
	}
	
}
