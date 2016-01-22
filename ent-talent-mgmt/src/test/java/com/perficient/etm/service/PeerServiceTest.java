package com.perficient.etm.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
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
import com.perficient.etm.repository.FeedbackRepository;
import com.perficient.etm.service.activiti.ProcessService;
import com.perficient.etm.utils.SpringAppTest;

public class PeerServiceTest extends SpringAppTest {

    @Mock
    private ReviewService reviewSvc;

    @Mock
    private ProcessService processSvc;

    @Mock
    private FeedbackRepository feedbackRepository;
    
    @InjectMocks
    private PeerService peerSvc;

    @Before
    public void initMocks() {
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
        User peer = mock(User.class);
        when(peer.getId()).thenReturn(3L);
        when(reviewSvc.findById(reviewId)).thenReturn(review);
        when(reviewSvc.getFeedbackForPeer(1L, 3L)).thenReturn(Optional.empty());
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
        when(reviewSvc.getFeedbackForPeer(reviewId, peerId)).thenReturn(Optional.empty());

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
        when(reviewSvc.getFeedbackForPeer(reviewId, peerId)).thenReturn(Optional.of(peerFeedback));
        when(feedbackRepository.save(peerFeedback)).thenReturn(peerFeedback);
        when(peerFeedback.getProcessId()).thenReturn(feedbackProcessId);
        when(processSvc.cancel(feedbackProcessId)).thenReturn(true);

        peerSvc.removePeerFeedback(reviewId, peerId);

        verify(processSvc).cancel(feedbackProcessId);
        verify(feedbackRepository).save(peerFeedback);
        verify(reviewSvc).update(review);
    }
}
