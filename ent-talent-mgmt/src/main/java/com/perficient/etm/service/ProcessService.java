package com.perficient.etm.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.activiti.engine.RuntimeService;
import org.springframework.stereotype.Service;

import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.ReviewType;

/**
 * Service for looking at process information from the process engine. A process is a workflow
 * composed of tasks.
 */
@Service
public class ProcessService {
	
	@Inject
	private RuntimeService runtimeSvc;

	public String initiateProcess(ReviewType reviewType, Review review) {
		/*
		 * Start the process in Activiti
		 * set the process ID in review
		 * set the status on the review
		 * save the review
		 */
		return null;
	}

	
	
}
