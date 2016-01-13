package com.perficient.etm.web.rest;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.perficient.etm.domain.User;
import com.perficient.etm.exception.InvalidRequestException;
import com.perficient.etm.service.PeerService;

/**
 * REST controller for managing Review.
 */
@RestController
@RequestMapping("/api")
public class PeerResource {

    private final Logger log = LoggerFactory.getLogger(PeerResource.class);

    @Inject
    private PeerService peerSvc;

    /**
     * POST  /reviews/:reviewId/peers -> Add peers to a review
     * @throws URISyntaxException
     */
    @RequestMapping(value = "reviews/{reviewId}/peers",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional
    public ResponseEntity<Void> save(@PathVariable Long reviewId, @RequestBody User peer, BindingResult result) throws URISyntaxException {
        log.debug("REST request to update peers for Review Id : {}", reviewId);
        if (result.hasErrors()) {
            throw new InvalidRequestException("Invalid review update", result);
        }
        peerSvc.addPeerFeedback(reviewId, peer);
        return ResponseEntity.created(new URI("/api/reviews/" + reviewId + "/peers/" + peer.getId())).build();
    }

    /**
     * DELETE  /reviews/:reviewId/peers -> Remove peers from a review
     * @throws URISyntaxException
     */
    @RequestMapping(value = "reviews/{reviewId}/peers/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long reviewId, @PathVariable Long id) throws URISyntaxException {
        log.debug("REST request to delete peer feedback for peer {} on review Id {}", id, reviewId);
        peerSvc.removePeerFeedback(reviewId, id);
        return ResponseEntity.ok().build();
    }

}
