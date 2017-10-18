package com.perficient.etm.service.activiti;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.security.test.context.support.WithUserDetails;

import com.perficient.etm.exception.ETMException;
import com.perficient.etm.exception.ReviewProcessNotFound;
import com.perficient.etm.service.ServicesTestUtils;
import com.perficient.etm.service.activiti.ProcessService;
import com.perficient.etm.service.activiti.ReviewTypeProcess;
import com.perficient.etm.utils.SpringAppTest;

public class ProcessServiceTest extends SpringAppTest {
    @Inject
    ProcessService processSvc;

    @Test
    @Ignore
    @WithUserDetails("dev.user3")
    public void testStartReviewProcess() throws ETMException {
        String instanceId = ServicesTestUtils.startAnnualReviewProcess(processSvc);
        assertNotNull("Instance of the processId should not be null", instanceId);
    }

    @Test(expected = ReviewProcessNotFound.class)
    public void testStartReviewProcessWithNonExistingType() throws ETMException {
        ReviewTypeProcess type = null;
        ServicesTestUtils.startReviewProcess(processSvc, type);
        Assert.fail("Instance id should not be returned for a non existing process");
    }

    @Test
    @Ignore
    @WithUserDetails("dev.user3")
    public void testCancel() throws ETMException {
        String instanceId = ServicesTestUtils.startAnnualReviewProcess(processSvc);
        boolean result = processSvc.cancel(instanceId);
        assertTrue("Cancelation of an existing process should return true", result);
    }

    @Test
    public void testCancelInstanceNotFound() {
        boolean result = processSvc.cancel("asdadad:1231313");
        assertFalse("Cancelation of a non existing process should return false",result);
    }

    @Test
    public void testStartPeerReview() {
        String id = ServicesTestUtils.startPeerReview(processSvc);
        assertNotNull("Id of the peer review process should not be null", id);
    }
    
}
