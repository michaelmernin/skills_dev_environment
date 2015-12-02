package com.perficient.etm.service;

import org.joda.time.LocalDate;
import org.mockito.Mockito;

import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.ReviewType;
import com.perficient.etm.domain.User;
import com.perficient.etm.exception.ETMException;
import com.perficient.etm.exception.ReviewProcessNotFound;
import com.perficient.etm.repository.ReviewTypeRepository;
import com.perficient.etm.service.activiti.ProcessService;
import com.perficient.etm.service.activiti.ReviewTypeProcess;

/**
 * Utility class that will help with common operations between
 * the Services Tests.
 * @author alex blanco <alex.blanco@perficient.com>
 */
public class ServicesTestUtils {

	private static final String DEFAULT_TITLE = "SAMPLE_TEXT";

    private static final LocalDate DEFAULT_START_DATE = new LocalDate(0L);

    private static final LocalDate DEFAULT_END_DATE = DEFAULT_START_DATE.plusYears(1);
    private static final String DEFAULT_CLIENT = "SAMPLE_TEXT";
    private static final String DEFAULT_PROJECT = "SAMPLE_TEXT";
    private static final String DEFAULT_ROLE = "SAMPLE_TEXT";
    private static final String DEFAULT_RESPONSIBILITIES = "SAMPLE_TEXT";

    private static final Double DEFAULT_RATING = 0.0;
	
	
	/**
	 * Starts a review process with the ReviewType of annual
	 * @param processSvc The ProcessService instance of Activiti engine that will
	 * be used to start the process
	 * @return String with the process id of the started process 
	 * @throws ReviewProcessNotFound In case the review type launched is not found
	 */
	public static String startAnnualReviewProcess(ProcessService processSvc) throws ETMException{
		ReviewTypeProcess type = createMockReviewType();
		return startReviewProcess(processSvc,type);
	}
	
	/**
	 * Starts a review process with the ReviewType as specified in the parameter
	 * @param processSvc The ProcessService instance of Activiti engine that will
	 * be used to start the process
	 * @return String with the process id of the started process 
	 * @throws ReviewProcessNotFound In case the review type launched is not found
	 */
	public static String startReviewProcess(ProcessService processSvc, ReviewTypeProcess type) throws ETMException{
		Review review = createMockReviewObject();
		
		String instanceId = processSvc.initiateProcess(type , review );
		return instanceId;
	}
	
	
	/**
	 * Method that will create two mock User objects for the review process.
	 * The first object in the result will be the reviewer and the second the
	 * reviewee. 
	 * @return Array of User objects with the mocks
	 */
	public static User[] createMockReviewUsers(){
		Review review = Mockito.mock(Review.class);
		User reviewer = Mockito.mock(User.class);
		Mockito.when(reviewer.getId()).thenReturn(1L);
		Mockito.when(reviewer.getEmail()).thenReturn("reviewer@perficient.com");
		Mockito.when(review.getReviewer()).thenReturn(reviewer);
		
		User reviewee = Mockito.mock(User.class);
		Mockito.when(reviewee.getId()).thenReturn(2L);
		Mockito.when(reviewee.getEmail()).thenReturn("reviewee@perficient.com");
		Mockito.when(review.getReviewee()).thenReturn(reviewee);
		
		return new User[]{reviewer, reviewee};
	}
	/**
	 * Creates a mock object of type Review in order to return a mock
	 * Reviewer and a Reviwee. 
	 * @return Mock Review object with the users
	 */
	public static Review createMockReviewObject(){
		Review review = Mockito.mock(Review.class);
		
		User[] users = createMockReviewUsers();
		Mockito.when(review.getReviewer()).thenReturn(users[0]);
		Mockito.when(review.getReviewee()).thenReturn(users[1]);
		
		return review;
	}
	/**
	 * Creates ReviewTypeProcess that will point to Annual Review 
	 * constant . 
	 * @return ReviewTypeProcess mock object
	 */
	public static ReviewTypeProcess createMockReviewType(){
		return ReviewTypeProcess.ANNUAL;
	}
	/**
	 * Invokes the createPeerReview method of the passed ProcessService
	 * object using two mock users and returns the id of the new process
	 * @param processSvc The ProcessService object where to invoke the
	 * method in
	 * @return The String id of the started process
	 */
	public static String startPeerReview(ProcessService processSvc){
		User[] users = createMockReviewUsers();
		return processSvc.createPeerReview(users[0], users[1]);
	}
	
	/**
	 *  Creates a new Review Object as if it was prepared to 
	 *  be created in the system.
	 */
	public static Review createNewReviewForTests(ReviewTypeRepository reviewRepository){
		Review review = new Review();
        review.setTitle(DEFAULT_TITLE);
        review.setStartDate(DEFAULT_START_DATE);
        review.setEndDate(DEFAULT_END_DATE);
        review.setClient(DEFAULT_CLIENT);
        review.setProject(DEFAULT_PROJECT);
        review.setRole(DEFAULT_ROLE);
        review.setResponsibilities(DEFAULT_RESPONSIBILITIES);
        review.setRating(DEFAULT_RATING);
        
        //Retrieve the annual review type from the repository as default
        ReviewType reviewType = reviewRepository.getOne(1L);
        //reviewType.setName("Annual Review");
		review.setReviewType(reviewType);
		
		return review;
	}
	
}
