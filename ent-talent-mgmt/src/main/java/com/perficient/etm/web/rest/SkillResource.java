package com.perficient.etm.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Skills of a User
 */
@RestController
@RequestMapping("/api")
public class SkillResource implements RestResource{
    
    private final Logger log = LoggerFactory.getLogger(SkillResource.class);
    
}
