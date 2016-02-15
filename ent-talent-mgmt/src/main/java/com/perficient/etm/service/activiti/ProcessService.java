package com.perficient.etm.service.activiti;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.perficient.etm.domain.Feedback;
import com.perficient.etm.domain.FeedbackStatus;
import com.perficient.etm.domain.Review;
import com.perficient.etm.exception.ActivitiProcessInitiationException;
import com.perficient.etm.exception.ETMException;
import com.perficient.etm.exception.MissingReviewInfoException;
import com.perficient.etm.exception.ReviewProcessNotFound;

/**
 * Service for looking at process information from the process engine. A process is a workflow
 * composed of tasks.
 */
@Service
public class ProcessService {

    Logger log = LoggerFactory.getLogger(ProcessService.class);

    @Inject
    private RuntimeService runtimeSvc;
    
    @Inject
    private TaskService taskSvc;
    
    public String initiateProcess(ReviewTypeProcess reviewType, Review review) throws ETMException {
        if (reviewType == null)
            throw new ReviewProcessNotFound("null");
        if (review.getReviewer() == null)
            throw new MissingReviewInfoException("review.revieweer");

        Map<String, Object> variables = new HashMap<>();
        variables.put(ProcessConstants.REVIEWER_VARIABLE, review.getReviewer().getId());
        variables.put(ProcessConstants.REVIEWEE_VARIABLE, review.getReviewee().getId());
        //variables.put(ProcessConstants.REVIEW_VARIABLE, review.getId());
        try {
            ProcessInstance processInstance = runtimeSvc.startProcessInstanceByKey(reviewType.getProcessId(), variables );
            return processInstance.getId();
        } catch(Exception e) {
            throw new ReviewProcessNotFound("Unable to start review process",e);
        }
    }
    
    public void addReviewId(String processId, Long reviewId) {
        runtimeSvc.setVariable(processId, ProcessConstants.REVIEW_VARIABLE, reviewId);
    }

    /**
     * Cancels a process with the specified process Id in the activiti engine.
     * It returns false if the process with the specified id was not found in the
     * engine
     * @param processId The String process Id in the activiti engine to cancel
     * @return True if it was cancelled correctly.
     */
    public boolean cancel(String processId) {
        try {
            runtimeSvc.deleteProcessInstance(processId, "Process Canceled");
            return true;
        } catch (ActivitiObjectNotFoundException ex) {
            log.info("Unable to cancel process with InstaceId {0}",processId);
        }
        return false;
    }

    /**
     * Starts a new Peer Review process in the acitivi engine and returns the
     * id of the new started process. This process is defined in the
     * peer_review.bpmn20.xml file.
     * @param feedback the Feedback object that will be related to the peer review
     * process
     * @return String with the id of the process started in the activiti engine
     */
    public String initiatePeerReview(Feedback feedback)  {
        Map<String, Object> variables = new HashMap<>();
        variables.put(ProcessConstants.PEER_VARIABLE, feedback.getAuthor().getId());
        variables.put(ProcessConstants.PEER_EMAIL_VARIABLE, feedback.getAuthor().getEmail());
        try{
            ProcessInstance pId =
                    runtimeSvc.startProcessInstanceByKey(ReviewTypeProcess.PEER.getProcessId(),variables );
            return pId.getId();
        }catch(Exception e){
            log.error("Exception while starting peer review process for feedback "+feedback,e);
            throw new ActivitiProcessInitiationException(e);
        }
    }

    public Feedback completePeerReviewTask(Feedback feedback, String result) {
        String pId = feedback.getProcessId();
        ProcessInstance processInstance = runtimeSvc.createProcessInstanceQuery().variableValueEquals("processId", pId)
            .singleResult();
        Task t = taskSvc.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskSvc.complete(t.getId());
        
        feedback.setFeedbackStatus(FeedbackStatus.COMPLETE);
        return feedback;
    }
}
