package com.perficient.etm.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

/**
 * REST controller to determine the environment - (ultimately spring profile)
 */
@RestController
@RequestMapping("/api")
public class EnvironmentResource {

    private final Logger log = LoggerFactory.getLogger(EnvironmentResource.class);

    @Inject
    private Environment env;

    /**
     * GET  /env -> get the active spring profiles for this environment
     */
    @RequestMapping(value = "/env",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public String[] getEnv() {
        log.debug("REST request to get env string");
        return env.getActiveProfiles();
    }
}
