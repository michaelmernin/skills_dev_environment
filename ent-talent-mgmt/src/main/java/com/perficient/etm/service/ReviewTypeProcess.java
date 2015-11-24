package com.perficient.etm.service;

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
		switch (type.getName()){
		case "Annual Review":
			return ANNUAL;
			default: return null;
		}
	}
	
}