package com.perficient.etm.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

/**
 * Service for looking at process information from the process engine. A process is a workflow
 * composed of tasks.
 */
@Service
public class ProcessService {
	public enum ProcessType { ANNUAL_REVIEW };
	
	/**
	 * Looks up the processes that are owned by the passed in user. This should not be
	 * confused with tasks. If the process is an annual review the owner should be the 
	 * target of the review, not the person with the currently assigned task. 
	 *  
	 * @param processType
	 * @param userId
	 * @return
	 */
	List<String> getProcesses(ProcessType processType, String userId) {
		return new ArrayList<String>();
	}
	
	
}
