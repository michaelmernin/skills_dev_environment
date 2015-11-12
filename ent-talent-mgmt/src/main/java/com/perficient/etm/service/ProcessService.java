package com.perficient.etm.service;

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

	public String initiateProcess(ReviewType reviewType, Review review) throws ReviewProcessNotFound {
		/*
		 * Start the process in Activiti
		 * set the process ID in review
		 * set the status on the review
		 * save the review
		 */
		ReviewTypeProcess processType = ReviewTypeProcess.fromReviewType(reviewType);
		if (processType == null)
			throw new ReviewProcessNotFound(reviewType);
		
		Map<String, Object> variables = new HashMap<>();
		variables.put("Reviewer", review.getReviewer().getId());
		variables.put("Reviewee", review.getReviewee().getId());
		
		ProcessInstance processInstance = runtimeSvc.startProcessInstanceByKey(processType.getProcessId(), variables );
		return processInstance.getId();
	}

	public boolean cancel(String processId){
		try{
			runtimeSvc.deleteProcessInstance(processId, "Process Canceled");
			return true;
		}catch (ActivitiObjectNotFoundException ex){
			log.info("Unable to cancel process with InstaceId {0}",processId);
		}
		return false;
	}
	
}
