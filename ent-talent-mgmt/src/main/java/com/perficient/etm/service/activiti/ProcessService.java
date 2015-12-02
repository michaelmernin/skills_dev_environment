package com.perficient.etm.service.activiti;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.ReviewType;
import com.perficient.etm.domain.User;
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

	public String initiateProcess(ReviewTypeProcess reviewType, Review review) throws ETMException {
		if (reviewType == null)
			throw new ReviewProcessNotFound("null");
		if (review.getReviewer() == null)
			throw new MissingReviewInfoException("review.revieweer");
		
		Map<String, Object> variables = new HashMap<>();
		variables.put("Reviewer", review.getReviewer().getId());
		variables.put("Reviewee", review.getReviewee().getId());
		try{
			ProcessInstance processInstance = runtimeSvc.startProcessInstanceByKey(reviewType.getProcessId(), variables );
			return processInstance.getId();
		}catch(Exception e){
			throw new ReviewProcessNotFound("Unable to start review process",e);
		}
	}

	/**
	 * Cancels a process with the specified process Id in the activiti engine.
	 * It returns false if the process with the specified id was not found in the
	 * engine
	 * @param processId The String process Id in the activiti engine to cancel
	 * @return True if it was cancelled correctly. 
	 */
	public boolean cancel(String processId){
		try{
			runtimeSvc.deleteProcessInstance(processId, "Process Canceled");
			return true;
		}catch (ActivitiObjectNotFoundException ex){
			log.info("Unable to cancel process with InstaceId {0}",processId);
		}
		return false;
	}
	
	/**
	 * Starts a new Peer Review process in the acitivi engine and returns the 
	 * id of the new started process. This process is defined in the 
	 * peer_review.bpmn20.xml file.
	 * @param reviewee The User this peer review is for
	 * @param peer The User (peer) that should give feedback to. The peer review
	 * task will be assigned to this user
	 * @return String with the id of the process started in the activiti engine
	 */
	public String createPeerReview(User reviewee, User peer){
		Map<String, Object> variables = new HashMap<>();
		variables.put("Reviewee",reviewee.getId());
		variables.put("Peer", peer.getId());
		variables.put("peerEmail", peer.getEmail());
		
		ProcessInstance pId = 
				runtimeSvc.startProcessInstanceByKey(ReviewTypeProcess.PEER.getProcessId(),variables );
		return pId.getId();
	}
	
}
