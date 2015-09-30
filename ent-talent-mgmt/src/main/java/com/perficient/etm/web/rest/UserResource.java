package com.perficient.etm.web.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.annotation.JsonView;
import com.perficient.etm.domain.User;
import com.perficient.etm.repository.UserRepository;
import com.perficient.etm.security.AuthoritiesConstants;
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
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api")
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Inject
    private UserRepository userRepository;

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
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
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
     * GET  /users/autocomplete/query -> get all users with name containing query
     */
    @RequestMapping(value = "/users/autocomplete/{query}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @JsonView(View.Public.class)
    List<User> getUsersAutocomplete(@PathVariable String query) {
        log.debug("REST request to get users for autocomplete");
        String[] splitQuery = query.split(" ");
        List<User> usersList = new ArrayList<User>();
        if (splitQuery.length > 1) {
          usersList.addAll(userRepository.findByFirstNameStartingWithAndLastNameStartingWith(splitQuery[0], splitQuery[1]));
        }
        usersList.addAll(userRepository.findByFirstNameStartingWith(query));
        usersList.addAll(userRepository.findByLastNameStartingWith(query));
        return usersList;
    }
}
