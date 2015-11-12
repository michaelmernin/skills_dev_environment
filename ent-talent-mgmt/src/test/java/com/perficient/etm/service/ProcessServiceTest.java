package com.perficient.etm.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.perficient.etm.domain.ReviewType;
import com.perficient.etm.exception.ReviewProcessNotFound;
import com.perficient.etm.utils.SpringAppTest;

public class ProcessServiceTest extends SpringAppTest {
	@Inject 
	ProcessService processSvc;
	
	@Test
	public void basicSetup() throws ReviewProcessNotFound {
		String instanceId = ServicesTestUtils.startAnnualReviewProcess(processSvc);
		assertNotNull("Instance of the processId should not be null", instanceId);
	}
	
	@Test(expected = ReviewProcessNotFound.class)
	public void testStartReviewProcessWithNonExistingType() throws ReviewProcessNotFound{
		ReviewType type = Mockito.mock(ReviewType.class);
		Mockito.when(type.getName()).thenReturn("Non Existing");
		
		ServicesTestUtils.startReviewProcess(processSvc, type);
		Assert.fail("Instance id should not be returned for a non existing process");
	}
	
	@Test
	public void testCancel() throws ReviewProcessNotFound{
		String instanceId = ServicesTestUtils.startAnnualReviewProcess(processSvc);
		boolean result = processSvc.cancel(instanceId);
		assertTrue("Cancelation of an existing process should return true", result);
	}
	
	@Test
	public void testCancelInstanceNotFound(){
		boolean result = processSvc.cancel("asdadad:1231313");
		assertFalse("Cancelation of a non existing process should return false",result);
	}
	
	
}
