package com.perficient.etm.service;

import static org.junit.Assert.assertEquals;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javassist.NotFoundException;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.perficient.etm.domain.Feedback;
import com.perficient.etm.domain.FeedbackType;
import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.User;
import com.perficient.etm.repository.FeedbackRepository;
import com.perficient.etm.service.activiti.ProcessService;
import com.perficient.etm.utils.SpringMockTest;

public class PeerServiceTest extends SpringMockTest {

    @Mock
    private ReviewService reviewSvc;

    @Mock
    private ProcessService processSvc;

    @Mock
    private FeedbackRepository feedbackRepository;
    
    @Mock
    private FeedbackService feedbackService;
    
    @InjectMocks
    private PeerService peerSvc;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PeerService.addPeerFeedback tests.
    @Test
    public void shouldAddPeerFeedback() {
        Long reviewId = 1L;
        Review review = mock(Review.class);
        Set<User> peers = new HashSet<User>();
        Feedback peerFeedback = mock(Feedback.class);
        Set<Feedback> feedbacks = new HashSet<Feedback>();
        User peer = mock(User.class);
        when(peer.getId()).thenReturn(3L);
        when(reviewSvc.findById(reviewId)).thenReturn(review);
        when(feedbackService.addFeedback(review, peer, FeedbackType.PEER)).thenReturn(peerFeedback);
        when(review.getPeers()).thenReturn(peers);
        when(review.getFeedback()).thenReturn(feedbacks);

        peerSvc.addPeerFeedback(reviewId, peer);

        verify(reviewSvc).update(review);
        assertEquals(1, peers.size());
        assertEquals(1, feedbacks.size());
        verify(feedbackService).addFeedback(review, peer, FeedbackType.PEER);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PeerService.removePeerFeedback tests.

    @Test
    public void shouldRemovePeer() {
        Long reviewId = 1L;
        Long peerId = 2L;
        Review review = mock(Review.class);
        User peer = mock(User.class);

        @SuppressWarnings("serial")
        Set<User> peers = new HashSet<User>() {{ add(peer);}};

        when(reviewSvc.findById(reviewId)).thenReturn(review);
        when(review.getPeers()).thenReturn(peers);
        when(peer.getId()).thenReturn(peerId);
        when(feedbackRepository.findOneByReviewIdAndAuthorId(reviewId, peerId)).thenReturn(Optional.empty());

        peerSvc.removePeerFeedback(reviewId, peerId);
        verify(reviewSvc).update(review);
    }

    @Test
    public void shouldRemovePeerFeedback() throws NotFoundException {
        Long reviewId = 1L;
        Long peerId = 2L;
        Review review = mock(Review.class);
        User peer = mock(User.class);

        @SuppressWarnings("serial")
        Set<User> peers = new HashSet<User>() {{ add(peer);}};

        Feedback peerFeedback = mock(Feedback.class);
        String feedbackProcessId = "fp1";
        
        when(reviewSvc.findById(reviewId)).thenReturn(review);
        when(review.getPeers()).thenReturn(peers);
        when(peer.getId()).thenReturn(peerId);
        when(feedbackRepository.findOneByReviewIdAndAuthorId(reviewId, peerId)).thenReturn(Optional.of(peerFeedback));
        when(feedbackService.closeFeedback(peerFeedback)).thenReturn(peerFeedback);
        when(peerFeedback.getProcessId()).thenReturn(feedbackProcessId);
        when(processSvc.cancel(feedbackProcessId)).thenReturn(true);

        peerSvc.removePeerFeedback(reviewId, peerId);

        verify(processSvc).cancel(feedbackProcessId);
        verify(feedbackService).closeFeedback(peerFeedback);
        verify(reviewSvc).update(review);
    }
}
