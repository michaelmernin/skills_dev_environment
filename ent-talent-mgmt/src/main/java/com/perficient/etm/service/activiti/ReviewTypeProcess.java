package com.perficient.etm.service.activiti;

import com.perficient.etm.domain.ReviewType;

public enum ReviewTypeProcess {
	
	ANNUAL("annualReview"),
	PEER("peerReview");
	
	private final String processId;
	
	private ReviewTypeProcess(String processId) {
		this.processId = processId;
	}

	public String getProcessId() {
		return processId;
	}
	
	public static ReviewTypeProcess fromReviewType(ReviewType type){
		if (type == null)
			return null;
		switch (type.getName()){
		case "Annual Review":
			return ANNUAL;
			default: return null;
		}
	}
	
}
