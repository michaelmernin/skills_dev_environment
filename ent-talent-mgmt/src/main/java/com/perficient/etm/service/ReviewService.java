package com.perficient.etm.service;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.perficient.etm.domain.Feedback;
import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.ReviewType;
import com.perficient.etm.domain.User;
import com.perficient.etm.exception.ReviewProcessNotFound;
import com.perficient.etm.repository.ReviewRepository;
import com.perficient.etm.service.activiti.ProcessService;
import com.perficient.etm.service.activiti.ReviewTypeProcess;

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
    private final Logger log = LoggerFactory.getLogger(ReviewService.class);

	/**
	 * The review repository that will be in charge 
	 * of managing the Review models in the database
	 */
	@Inject
	private ReviewRepository reviewRepository;
	
	@Inject
	private ProcessService processSvc;
	
    @Inject
    private UserService userSvc;
	
	
	
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
	public Review startReviewProcess(Review review) throws ReviewProcessNotFound{
		getLog().info("Starting a new review process");
		
		getLog().debug("Sanitizing the review obejct");
		review.sanitize(true);
		
		ReviewType type = review.getReviewType();
		ReviewTypeProcess processType = ReviewTypeProcess.fromReviewType(type);
		if (processType == null)
			throw new ReviewProcessNotFound(type);
		
		String id = processSvc.initiateProcess(processType, review);
		review.setReviewProcessId(id);

        review = reviewRepository.save(review);
        
		return review;
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
	
}
