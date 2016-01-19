package com.perficient.etm.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.User;
import com.perficient.etm.exception.ResourceNotFoundException;
import com.perficient.etm.repository.ReviewRepository;
import com.perficient.etm.repository.UserRepository;
import com.perficient.etm.security.AuthoritiesConstants;
import com.perficient.etm.security.SecurityUtils;
import com.perficient.etm.web.view.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api")
public class UserResource implements RestResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Inject
    private UserRepository userRepository;

    @Inject
    private ReviewRepository reviewRepository;

    /**
     * GET  /users -> get all the users.
     */
    @RequestMapping(value = "/users",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    List<User> getAll() {
        log.debug("REST request to get all Users");
        return userRepository.findAllNormalUsers();
    }

    /**
     * GET  /users/:login -> get the "login" user.
     */
    @RequestMapping(value = "/users/{login:.+}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    ResponseEntity<User> getUser(@PathVariable String login) {
        log.debug("REST request to get User : {}", login);
        return userRepository.findOneByLogin(login)
                .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("User " + login + " cannot be found.");
                });
    }

    /**
     * PUT  /users/:id -> Updates an existing user.
     */
    @RequestMapping(value = "/users/{id}",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> update(@RequestBody User user) throws URISyntaxException {
        log.debug("REST request to update USER : {}", user);
        if (user.getId() == null) {
            return ResponseEntity.unprocessableEntity().build();
        }
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE  /users/:id -> delete the "id" user.
     */
    @RequestMapping(value = "/users/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete User : {}", id);
        userRepository.delete(id);
    }

    /**
     * GET  /counselees -> get all the counselees of the current user.
     */
    @RequestMapping(value = "/counselees",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.COUNSELOR)
    @JsonView(View.Counselee.class)
    List<User> getCounselees() {
        log.debug("REST request to get user's counselees");
        return userRepository.findCounseleesForCurrentUser();
    }

    /**
     * GET  /profile -> get the profile of the current user.
     */
    @RequestMapping(value = "/profile",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    ResponseEntity<User> getProfile() {
        log.debug("REST request to get Profile");
        return getUser(SecurityUtils.getCurrentLogin());
    }

    /**
     * GET  /users/autocomplete/query -> get all users with name containing query
     */
    @RequestMapping(value = "/users/autocomplete",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @JsonView(View.Public.class)
    List<User> getUsersAutocomplete(@RequestParam String query, @RequestParam Long reviewId) {
        log.debug("REST request to get users for autocomplete");
        String[] splitQuery = query.split(" ");
        List<User> usersList = new ArrayList<User>();
        if (splitQuery.length > 1) {
          usersList.addAll(userRepository.findUsersForAutocompleteByFullName(splitQuery[0], splitQuery[1]));
        } else {
          usersList.addAll(userRepository.findUsersForAutocomplete(query));
        }
        Review review = reviewRepository.findOne(reviewId);
        User reviewee = review.getReviewee();
        User reviewer = review.getReviewer();
        Set<User> peers = review.getPeers();
        List<User> filteredUsersList = new ArrayList<User>();
        for (User u : usersList) {
          if (!peers.contains(u) & u.getId() != reviewee.getId() & u.getId() != reviewer.getId()) {
            filteredUsersList.add(u);
          }
        }
        return filteredUsersList;
    }
}
