package com.perficient.etm.security;

import java.util.Set;

import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.User;

public class PrePostUtils {

/*	public static final String POST_FILTER_REVIEWS = "filterObject.reviewee.login == principal.username"
												+ " or filterObject.reviewer.login == principal.username"
												+ " or filterObject.reviewee.counselor.login == principal.username"
												+ " or filterObject.reviewee.generalManager.login == principal.username"
												+ " or (filterObject.peers.![login].contains(principal.username) and filterObject.reviewStatus.id < 4) ";
	
	public static final String POST_AUTHORIZE_REVIEW = "returnObject == null"
													+ " or returnObject.reviewee.login == principal.username"
													+ " or returnObject.reviewer.login == principal.username"
													+ " or returnObject.reviewee.counselor.login == principal.username"
													+ " or returnObject.reviewee.generalManager.login == principal.username"
													+ " or (returnObject.peers.![login].contains(principal.username) and returnObject.reviewStatus.id < 4) ";
*/
	public static class Reviews{
		
		public static final String AUTHORIZE = "T(com.perficient.etm.security.PrePostUtils.Reviews).authorize(returnObject)";
		public static final String FILTER = "T(com.perficient.etm.security.PrePostUtils.Reviews).authorize(filterObject)";
		public static boolean authorize(Review review) {
			 return SecurityUtils.getPrincipal()
					.map(u -> {
						String currentUsername = u.getUsername();
						boolean reviewerCheck = review.getReviewer().getLogin().equals(currentUsername);
						boolean revieweeCheck = review.getReviewee().getLogin().equals(currentUsername);
						boolean councelorCheck = review.getReviewee().getCounselor().getLogin().equals(currentUsername);
						boolean generalManagerCheck = review.getReviewee().getGeneralManager().getLogin().equals(currentUsername);
						boolean peerCheck = peerCheck(review.getPeers(), currentUsername);
						
						return (reviewerCheck || revieweeCheck || councelorCheck || generalManagerCheck ||  peerCheck);
					}).orElse(false);
		}
		
		private static boolean peerCheck(Set<User> peers, String username) {
			return peers.stream().filter(u -> {
				return u.getLogin().equals(username);
			}).findAny().isPresent();
		}
	}

}
