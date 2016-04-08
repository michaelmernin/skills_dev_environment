package com.perficient.etm.service;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javassist.NotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import com.perficient.etm.domain.Feedback;
import com.perficient.etm.domain.FeedbackStatus;
import com.perficient.etm.domain.FeedbackType;
import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.User;
import com.perficient.etm.exception.ActivitiProcessInitiationException;
import com.perficient.etm.repository.FeedbackRepository;
import com.perficient.etm.repository.UserRepository;
import com.perficient.etm.security.UserDetailsService;
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
    
    @Mock
    private MailService mailService;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserDetailsService userDetailsService;
    
    @InjectMocks
    private PeerService peerSvc;
    
    public static final String SYSTEM_USERNAME = "system";

    @Before
    public void init(){
        when(processSvc.initiatePeerReview(anyObject())).thenReturn("Process");
    }
    
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
        User system = mock(User.class);
        when(peer.getId()).thenReturn(3L);
        when(reviewSvc.findById(reviewId)).thenReturn(review);
        when(feedbackService.addFeedback(review, peer, FeedbackType.PEER)).thenReturn(peerFeedback);
        when(review.getPeers()).thenReturn(peers);
        when(review.getFeedback()).thenReturn(feedbacks);
        when(userRepository.findOneByLogin(SYSTEM_USERNAME)).thenReturn(Optional.ofNullable(system));
        when(system.getLogin()).thenReturn("system");

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
        User system = mock(User.class);

        @SuppressWarnings("serial")
        Set<User> peers = new HashSet<User>() {{ add(peer);}};

        when(reviewSvc.findById(reviewId)).thenReturn(review);
        when(review.getPeers()).thenReturn(peers);
        when(peer.getId()).thenReturn(peerId);
        when(feedbackRepository.findOneByReviewIdAndAuthorId(reviewId, peerId)).thenReturn(Optional.empty());
        when(userRepository.findOneByLogin(SYSTEM_USERNAME)).thenReturn(Optional.ofNullable(system));
        when(system.getLogin()).thenReturn("system");

        peerSvc.removePeerFeedback(reviewId, peerId);
        verify(reviewSvc).update(review);
    }

    @Test
    public void shouldRemovePeerFeedback() throws NotFoundException {
        Long reviewId = 1L;
        Long peerId = 2L;
        Review review = mock(Review.class);
        User peer = mock(User.class);
        User system = mock(User.class);

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
        when(userRepository.findOneByLogin(SYSTEM_USERNAME)).thenReturn(Optional.ofNullable(system));
        when(system.getLogin()).thenReturn("system");

        peerSvc.removePeerFeedback(reviewId, peerId);

        verify(processSvc).cancel(feedbackProcessId);
        verify(reviewSvc).update(review);
    }
    
    @Test
    public void startPeerReviewProcess() {
        Feedback feedback = mock(Feedback.class);
        User peer = mock(User.class);
        when(peer.getId()).thenReturn(1L);
        when(peer.getEmail()).thenReturn("peer@test.com");
        when(feedback.getAuthor()).thenReturn(peer);
        when(feedbackRepository.save(feedback)).thenReturn(feedback);
        peerSvc.startPeerProcess(feedback);        
    }
    
    @Test(expected=ActivitiProcessInitiationException.class)
    public void startPeerReviewProcessOnStartedFeedback() {
        Feedback feedback = mock(Feedback.class);
        when(feedback.getFeedbackStatus()).thenReturn(FeedbackStatus.OPEN);
        
        peerSvc.startPeerProcess(feedback);
    }
    
}
