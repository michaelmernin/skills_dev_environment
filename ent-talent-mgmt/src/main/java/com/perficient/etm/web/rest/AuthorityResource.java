package com.perficient.etm.web.rest;

import java.util.List;

import java.util.stream.Collectors;

import com.codahale.metrics.annotation.Timed;
import com.perficient.etm.domain.Authority;
import com.perficient.etm.repository.AuthorityRepository;
import com.perficient.etm.security.AuthoritiesConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api")
public class AuthorityResource {

    private final Logger log = LoggerFactory.getLogger(AuthorityResource.class);

    @Inject
    private AuthorityRepository authorityRepository;

    /**
     * GET  /authorities -> get all the authorities.
     */
    @RequestMapping(value = "/authorities",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    List<Authority> getAll() {
        log.debug("REST request to get all Authorities");
        return authorityRepository.findAll().stream().filter(a -> {
            return !AuthoritiesConstants.ANONYMOUS.equals(a.getName());
        }).collect(Collectors.toList());
    }
}
