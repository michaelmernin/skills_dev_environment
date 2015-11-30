package com.perficient.etm.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javassist.NotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.perficient.etm.domain.Feedback;
import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.User;
import com.perficient.etm.exception.ResourceNotFoundException;
import com.perficient.etm.repository.FeedbackRepository;
import com.perficient.etm.repository.ReviewRepository;
import com.perficient.etm.repository.UserRepository;
import com.perficient.etm.service.activiti.ProcessService;
import com.perficient.etm.utils.SpringAppTest;

public class PeerServiceTest extends SpringAppTest {

	@InjectMocks
	PeerService peerSvc;
	
	@Mock
	private ReviewService reviewSvc;
	
	@Mock
	private ProcessService processSvc;
	
	@Mock
	private UserService userSvc;
	
	@Mock
	private ReviewRepository reviewRepository;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private FeedbackRepository feedbackRepository;
	
	@Before
    public void initMocks(){
        MockitoAnnotations.initMocks(this);
    }
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PeerService.addPeerFeedback tests.
	@Test
	public void shouldAddPeerFeedback() {
		Long reviewId = 1L;
		Review review = mock(Review.class);
		Set<User> peers = new HashSet<User>();
		Set<Feedback> feedbacks = new HashSet<Feedback>();
		Long peerId = 2L;
		String employeeId = "empId";
		User peer = mock(User.class);
		when(peer.getEmployeeId()).thenReturn(employeeId);
		when(reviewSvc.getReview(reviewId)).thenReturn(review);
		when(review.getPeers()).thenReturn(peers);
		when(review.getFeedback()).thenReturn(feedbacks);
		peerSvc.addPeerFeedback(reviewId, peer);
		
		verify(reviewSvc).update(review);
		assertEquals(1, peers.size());
		assertEquals(1, feedbacks.size());
		Feedback f = feedbacks.stream().findFirst().get();
		verify(feedbackRepository).save(f);
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PeerService.removePeerFeedback tests.
	
	@Test(expected=ResourceNotFoundException.class)
	public void expectReviewNotFoundRemovingPeerFeedback() throws ResourceNotFoundException {
		Long reviewId = 1L;
		Long peerId = 2L;
		
		when(reviewSvc.getFeedbackForPeer(reviewId, peerId)).thenReturn(Optional.empty());		

		peerSvc.removePeerFeedback(reviewId, peerId);
		
	}
	
	@Test
	public void shouldRemovePeerFeedback() throws NotFoundException {
		Long reviewId = 1L;
		Review review = mock(Review.class);
		
		Long peerId = 2L;
		User peer = mock(User.class);
		@SuppressWarnings("serial")
		Set<User> peers = new HashSet<User>() {{ add(peer);}};
		
		Feedback peerFeedback = mock(Feedback.class);
		String feedbackProcessId = "fp1";
		
		when(reviewSvc.getFeedbackForPeer(reviewId, peerId)).thenReturn(Optional.of(peerFeedback));		
		when(peerFeedback.getFeedbackProcessId()).thenReturn(feedbackProcessId);
		when(processSvc.cancel(feedbackProcessId)).thenReturn(true);
		when(reviewSvc.getReview(reviewId)).thenReturn(review);
		when(userSvc.getUser(peerId)).thenReturn(peer);
		when(review.getPeers()).thenReturn(peers);

		peerSvc.removePeerFeedback(reviewId, peerId);
		
		verify(processSvc).cancel(feedbackProcessId);
		// TODO: assert that the feedback status has been updated to cancelled
		verify(reviewSvc).update(review);
    }

}
