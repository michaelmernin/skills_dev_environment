package com.perficient.etm.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.perficient.etm.domain.ReviewAudit;
import com.perficient.etm.exception.ResourceNotFoundException;
import com.perficient.etm.repository.ReviewAuditRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing ReviewAudit.
 */
@RestController
@RequestMapping("/api")
public class ReviewAuditResource implements RestResource {

    private final Logger log = LoggerFactory.getLogger(ReviewAuditResource.class);

    @Inject
    private ReviewAuditRepository reviewAuditRepository;

    /**
     * POST  /reviewAudits -> Create a new reviewAudit.
     */
    @RequestMapping(value = "/reviewAudits",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ReviewAudit> createReviewAudit(@RequestBody ReviewAudit reviewAudit) throws URISyntaxException {
        log.debug("REST request to save ReviewAudit : {}", reviewAudit);
        if (reviewAudit.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new reviewAudit cannot already have an ID").body(null);
        }
        ReviewAudit result = reviewAuditRepository.save(reviewAudit);
        return ResponseEntity.created(new URI("/api/reviewAudits/" + result.getId()))
            //.headers(HeaderUtil.createEntityCreationAlert("reviewAudit", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /reviewAudits -> Updates an existing reviewAudit.
     */
    @RequestMapping(value = "/reviewAudits",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ReviewAudit> updateReviewAudit(@RequestBody ReviewAudit reviewAudit) throws URISyntaxException {
        log.debug("REST request to update ReviewAudit : {}", reviewAudit);
        if (reviewAudit.getId() == null) {
            return createReviewAudit(reviewAudit);
        }
        ReviewAudit result = reviewAuditRepository.save(reviewAudit);
        return ResponseEntity.ok()
            //.headers(HeaderUtil.createEntityUpdateAlert("reviewAudit", reviewAudit.getId().toString()))
            .body(result);
    }

    /**
     * GET  /reviewAudits -> get all the reviewAudits.
     */
    @RequestMapping(value = "/reviewAudits",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ReviewAudit>> getAllReviewAudits(Pageable pageable)
        throws URISyntaxException {
        Page<ReviewAudit> page = reviewAuditRepository.findAll(pageable);
        return new ResponseEntity<>(page.getContent(), HttpStatus.OK);
    }

    /**
     * GET  /reviewAudits/:id -> get the "id" reviewAudit.
     */
    @RequestMapping(value = "/reviewAudits/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ReviewAudit> getReviewAudit(@PathVariable Long id) {
        log.debug("REST request to get ReviewAudit : {}", id);
        return Optional.ofNullable(reviewAuditRepository.findOne(id))
            .map(reviewAudit -> new ResponseEntity<>(
                reviewAudit,
                HttpStatus.OK))
            .orElseThrow(() -> {
                return new ResourceNotFoundException("Review Audit " + id + " cannot be found.");
            });
    }

    /**
     * DELETE  /reviewAudits/:id -> delete the "id" reviewAudit.
     */
    @RequestMapping(value = "/reviewAudits/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteReviewAudit(@PathVariable Long id) {
        log.debug("REST request to delete ReviewAudit : {}", id);
        reviewAuditRepository.delete(id);
        return ResponseEntity.ok()
                //.headers(HeaderUtil.createEntityDeletionAlert("reviewAudit", id.toString()))
                .build();
    }
}
