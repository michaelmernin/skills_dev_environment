package com.perficient.etm.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import com.perficient.etm.domain.Review;
import com.perficient.etm.exception.ActivitiProcessInitiationException;
import com.perficient.etm.exception.ETMException;
import com.perficient.etm.exception.ReviewProcessNotFound;
import com.perficient.etm.repository.ReviewRepository;
import com.perficient.etm.repository.ReviewTypeRepository;
import com.perficient.etm.repository.UserRepository;
import com.perficient.etm.service.activiti.ProcessService;
import com.perficient.etm.service.activiti.TasksService;
import com.perficient.etm.utils.SpringAppTest;
/**
 * JUnit for the ReviewService class
 * @author Alexandro Blanco <alex.blanco@perficient.com>
 *
 */
@WithUserDetails("dev.user2")
public class ReviewServiceTest extends SpringAppTest {

    @Inject
    private ReviewTypeRepository reviewTypeRepo;

    @Inject
    private UserRepository userRepository;
    
    @Inject
    private ReviewRepository reviewRepository;

    @Inject
    private FeedbackService feedbackService;

    @Mock
    private ProcessService processSvc;

    @Mock
    private TasksService taskSvc;
    
    @InjectMocks
    private ReviewService reviewSvc;

    private Review review;

    @Before
    public void init() throws ETMException {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(reviewSvc, "userRepository", userRepository);
        ReflectionTestUtils.setField(reviewSvc, "reviewRepository", reviewRepository);
        ReflectionTestUtils.setField(reviewSvc, "feedbackService", feedbackService);
        review = ServicesTestUtils.createNewReviewForTests(reviewTypeRepo);
        review.setReviewee(userRepository.findOne(7L));

        List<org.activiti.engine.task.Task> mockTasksList =  new ArrayList<>();

        Mockito.when(taskSvc.getUserTasks(Mockito.anyLong())).thenReturn(mockTasksList);
    }

    @Test
    public void testStartReviewProcess() throws ETMException {
        Mockito.when(processSvc.initiateProcess(Mockito.any(), Mockito.any())).thenReturn("ProcessId[5]");
        
        reviewSvc.startReviewProcess(review);

        assertNotNull("A New Review object should have been created in db and it's id should be set in ReviewProcessId",
                review.getProcessId());
    }

    @Test(expected=ActivitiProcessInitiationException.class)
    public void testExceptionDuringInitiation() throws ETMException {
        Mockito.when(processSvc.initiateProcess(Mockito.any(), Mockito.any())).thenThrow(new RuntimeException("Test"));
        
        reviewSvc.startReviewProcess(review);
        
        assertNull("No process id should be set", review.getProcessId());
    }

    @Test(expected=ReviewProcessNotFound.class)
    public void testStartProcessWithReviewNoType() throws ETMException {
        review.setReviewType(null);
        reviewSvc.startReviewProcess(review);
    }

    @Test
    public void testUpdate() {
        reviewSvc.update(review);
    }

    @Test
    public void testFindAll() {
        List<Review> list = reviewSvc.findAll();
        
        assertNotNull(list);
    }
}
