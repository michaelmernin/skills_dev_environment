package com.perficient.etm.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;

import com.perficient.etm.domain.Feedback;
import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.ReviewStatus;
import com.perficient.etm.domain.ReviewType;
import com.perficient.etm.domain.User;
import com.perficient.etm.exception.ActivitiProcessInitiationException;
import com.perficient.etm.exception.ETMException;
import com.perficient.etm.exception.ReviewProcessNotFound;
import com.perficient.etm.repository.ReviewAuditRepository;
import com.perficient.etm.repository.ReviewRepository;
import com.perficient.etm.service.activiti.ProcessService;
import com.perficient.etm.service.activiti.ReviewTypeProcess;
import com.perficient.etm.service.activiti.TasksService;
import com.perficient.etm.web.view.ToDo;

/**
 * Service class to manage the available operations for the 
 * reviews in the system. This service will be the middle 
 * layer between the application controllers and the activiti
 * back-end services in order to manage the review processes.
 * 
 * @author Alexandro Blanco <alex.blanco@perficient.com>
 *
 */
@Service
public class ReviewService extends AbstractBaseService{
    
	/**
	 * The review repository that will be in charge 
	 * of managing the Review models in the database
	 */
	@Inject
	private ReviewRepository reviewRepository;
	
	@Inject
	private ProcessService processSvc;
	
	@Inject
	private TasksService tasksService;
	
	@Inject
	private UserService userSvc;
	
	@Inject
	private ReviewAuditRepository reviewAuditRepo;
	
	public ReviewService() {
		super();
		getLog().info("Creating review service.....");
	}

	/**
	 * Starts a new review process in the back-end service
	 * @param review The Review object where to extract the details
	 * from in order to start the process in the service
	 * @throws ReviewProcessNotFound If the type of process to be started
	 * has not been registered in the system
	 */
	public Review startReviewProcess(Review review) throws ETMException{
		getLog().info("Starting a new review process");
		
		getLog().debug("Sanitizing the review obejct");
		review.sanitize(true);
		
		ReviewType type = review.getReviewType();
		ReviewTypeProcess processType = ReviewTypeProcess.fromReviewType(type);
		if (processType == null)
			throw new ReviewProcessNotFound(type);
		try{
		    assignReviewer(review);
		    review.setReviewStatus(ReviewStatus.OPEN);
			String id = processSvc.initiateProcess(processType, review);
			review.setReviewProcessId(id);
	        review = reviewRepository.save(review);
		}catch(Exception e){
			getLog().warn("Exception while launching the review process in activiti",e);
			throw new ActivitiProcessInitiationException(e);
		}
		return review;
	}
	
	private void assignReviewer(Review review) {
        switch(review.getReviewType().getInterval()) {
        case ANNUAL:
            review.setReviewer(review.getReviewee().getCounselor());
            break;
        default:
            break;
        }
    }

    /**
	 * Will return all the Review objects from the review repository
	 * based on the criteria of the applied Filter in the ReviewRepository
	 * object associated with this service
	 * @return List of Review objects
	 */
	public List<Review> findAll() {
		return reviewRepository.findAll();
	}

	/**
	 * Returns the Review object with the passed id
	 * (this method delegates the work to the ReviewRepository.findOne method)
	 * @param id The Long value of the id for the Review object to locate
	 * @return Review object if found null otherwise
	 */
	public Review findById(Long id){
		return reviewRepository.findOne(id);
	}
	
	/**
	 * Deletes the review Object from the database with the specified Long id
	 * (this method delegates the work to the ReviewRepository.delete method)
	 * @param id The Long value of the id for the Review object to locate
	 */
	public void delete(Long id){
		reviewRepository.delete(id);
	}
	
	/**
	 * Saves the updated Review object in the database using the associated
	 * ReviewRepository object
	 * @param review The Review object to be updated
	 */
	public void update(Review review) {
		review.sanitize(false);
        review = reviewRepository.save(review);
	}
	
	/**
	 * Retrieves the feedback on this review for a given peer
	 * @param reviewId
	 * @param peerId
	 * @return
	 */
	public Optional<Feedback> getFeedbackForPeer(Long reviewId, Long peerId) {
        Review review = reviewRepository.getOne(reviewId);
        User peer = userSvc.getUser(peerId);
        Optional<Feedback> feedback = 
        		review.getFeedback().stream().filter(f->{return f.belongsToAuthor(peer);}).findFirst();
		return feedback;
	}
	
	/**
	 * Returns the list of ToDo objects assigned to a user across the 
	 * activiti processes that are started in the system
	 * @return List of ToDo objects representing the tasks 
	 * for the user
	 */
	public List<ToDo> getUsersReviewTodo(final User user){
		if (user == null)
			return Arrays.asList();
		List<Task> tasks = tasksService.getTasks(String.valueOf(user.getId()));
		List<ToDo> todos = tasks.stream().map(t->{ return ToDo.fromTask(t, user);}).collect(Collectors.toList());
		
		return todos;
	}
	
	/**
	 * Returns the List of ToDo objects related to the current user registarted
	 * in the SecurityContext
	 * @return List of ToDo objects representing the tasks 
	 * for the user
	 */
	public List<ToDo> getCurrentUserReviewTodo(){
		Optional<User> userOpt = userSvc.getUserFromLogin();
		return getUsersReviewTodo(userOpt.orElse(null));
	}
	
	
	//Getters and Setters
	public ReviewRepository getReviewRepository() {
		return reviewRepository;
	}

	public void setReviewRepository(ReviewRepository reviewRepository) {
		this.reviewRepository = reviewRepository;
	}

	public ProcessService getProcessSvc() {
		return processSvc;
	}

	public void setProcessSvc(ProcessService processSvc) {
		this.processSvc = processSvc;
	}

	public TasksService getTasksService() {
		return tasksService;
	}

	public void setTasksService(TasksService tasksService) {
		this.tasksService = tasksService;
	}

	public UserService getUserSvc() {
		return userSvc;
	}

	public void setUserSvc(UserService userSvc) {
		this.userSvc = userSvc;
	}

	public ReviewAuditRepository getReviewAuditRepo() {
		return reviewAuditRepo;
	}

	public void setReviewAuditRepo(ReviewAuditRepository reviewAuditRepo) {
		this.reviewAuditRepo = reviewAuditRepo;
	}
	
}
