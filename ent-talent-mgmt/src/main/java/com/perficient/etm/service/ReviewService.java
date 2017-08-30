package com.perficient.etm.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import javax.inject.Inject;


import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;


import com.perficient.etm.domain.Feedback;
import com.perficient.etm.domain.FeedbackType;
import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.ReviewStatus;
import com.perficient.etm.domain.ReviewType;
import com.perficient.etm.domain.User;
import com.perficient.etm.exception.ActivitiProcessInitiationException;
import com.perficient.etm.exception.ETMException;
import com.perficient.etm.exception.ReviewProcessNotFound;
import com.perficient.etm.repository.FeedbackRepository;
import com.perficient.etm.repository.ReviewAuditRepository;
import com.perficient.etm.repository.ReviewRepository;
import com.perficient.etm.repository.UserRepository;
import com.perficient.etm.security.SecurityUtils;
import com.perficient.etm.service.activiti.ProcessService;
import com.perficient.etm.service.activiti.ReviewTypeProcess;
import com.perficient.etm.service.activiti.TasksService;

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
public class ReviewService extends AbstractBaseService {

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
    private UserRepository userRepository;

    @Inject
    private ReviewAuditRepository reviewAuditRepo;
    
    @Inject
    private FeedbackService feedbackService;
    
    @Inject
    private FeedbackRepository feedbackRepository;

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
    public Review startReviewProcess(Review review) throws ETMException {
        getLog().info("Starting a new review process");

        ReviewTypeProcess processType = getProcessType(review);
        populateUsers(review);
        review.setReviewStatus(Optional.ofNullable(review.getReviewStatus()).orElse(ReviewStatus.OPEN));

        try {
        	// Saving here because we need the review id
        	review = reviewRepository.save(review);
            String id = processSvc.initiateProcess(processType, review);
            review.setProcessId(id);
            review = reviewRepository.save(review);
            Pair<Long, Long> ids = createFeedback(review);
            processSvc.addFeedbackIds(id, ids.getLeft(), ids.getRight());
        } catch (Exception e) {
            getLog().warn("Exception while launching the review process in activiti",e);
            throw new ActivitiProcessInitiationException(e);
        }

        return review;
    }
    
    public Review setStatusById(Long id, ReviewStatus status) {
        return Optional.ofNullable(reviewRepository.getOne(id))
            .map(r -> {
                r.setReviewStatus(status);
                return reviewRepository.save(r);
            }).orElse(null);
    }
    
    public Review setReviewAndNonPeerFeedbackStatusById(Long id, ReviewStatus status) {
        Review review = Optional.ofNullable(reviewRepository.getOne(id))
            .map(r -> {
                r.setReviewStatus(status);
                return reviewRepository.save(r);
            }).orElse(null);
        Optional.ofNullable(feedbackService.getAllFeedbackByReviewId(id))
            .map(List::stream)
            .orElseGet(Stream::empty)
            .filter(feedback -> feedback.getAuthor().getId() == review.getReviewee().getId() || feedback.getAuthor().getId() == review.getReviewer().getId())
            .map(feedback -> {
                feedback.setFeedbackStatus(feedbackService.initialFeedbackStatus(feedback.getFeedbackType()));
                return feedbackRepository.save(feedback);  
            })
            .collect(Collectors.toList());
        return review;
    }

    private Pair<Long, Long> createFeedback(final Review review) {
        final MutablePair<Long, Long> ids = new MutablePair<>();
        SecurityUtils.runAsSystem(userRepository, () -> {
            Feedback reviewerFeedback = feedbackService.addFeedback(review, review.getReviewer(), FeedbackType.REVIEWER);
            ids.setRight(reviewerFeedback.getId());
        });
        return ids;
    }

    private ReviewTypeProcess getProcessType(Review review) {
        ReviewType type = review.getReviewType();
        ReviewTypeProcess processType = ReviewTypeProcess.fromReviewType(type);

        if (processType == null) {
            throw new ReviewProcessNotFound(type);
        }
        return processType;
    }

    private void populateUsers(Review review) {
        Optional.ofNullable(review.getReviewee()).map(u -> {
            return u.getId();
        }).map(id -> {
            return userRepository.findOne(id);
        }).ifPresent(u -> {
            review.setReviewee(u);
            switch(review.getReviewType().getInterval()) {
            case ANNUAL:
                review.setReviewer(u.getCounselor());
                break;
            case ENGAGEMENT:
                break;
            default:
                break;
            }
        });
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
    
    public List<Review> findAllByReviewTypeAndRevieweeId(ReviewType reviewType, long id) {
        return reviewRepository.findAllByReviewTypeAndRevieweeId(reviewType, id);
    }

    /**
     * Returns the Review object with the passed id
     * (this method delegates the work to the ReviewRepository.findOne method)
     * @param id The Long value of the id for the Review object to locate
     * @return Review object if found null otherwise
     */
    public Review findById(Long id) {
        return reviewRepository.findOne(id);
    }

    /**
     * Deletes the review Object from the database with the specified Long id
     * (this method delegates the work to the ReviewRepository.delete method)
     * @param id The Long value of the id for the Review object to locate
     */
    public void delete(Long id) {
        // TODO set feedback and review as closed;
    }

    /**
     * Saves the updated Review object in the database using the associated
     * ReviewRepository object
     * @param review The Review object to be updated
     */
    public void update(Review review) {
        review = reviewRepository.save(review);
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
