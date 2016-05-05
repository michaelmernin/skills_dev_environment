package com.perficient.etm.web.rest;

import java.util.Optional;

import javax.inject.Inject;

import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.perficient.etm.domain.SkillRanking;
import com.perficient.etm.domain.SkillRankingHistory;
import com.perficient.etm.domain.User;
import com.perficient.etm.exception.ResourceNotFoundException;
import com.perficient.etm.repository.SkillRankingHistoryRepository;
import com.perficient.etm.repository.SkillRankingRepository;
import com.perficient.etm.service.UserService;

/**
 * REST controller for managing SkillsRanking of a User
 */

@RestController
@RequestMapping("/api")
public class SkillRankingResource implements RestResource{
    
    private final Logger log = LoggerFactory.getLogger(SkillRankingResource.class);
     
    @Inject
    private SkillRankingRepository skillRankingRepository;
    
    @Inject
    private SkillRankingHistoryRepository skillRankingHistoryRepository;
    
    @Inject
    private UserService userService;
    
    /**
    * POST /skillRanking -> create a skill Ranking
    */
   @RequestMapping(value = "/skillRanking",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
   @Timed
   @ResponseBody
   public ResponseEntity<SkillRanking> saveSkillRanking(@RequestBody SkillRanking skillRanking,  BindingResult result) {
       log.debug("REST request to create a skill ranking : {}", skillRanking);
       return userService.getUserFromLogin().map(user -> {
           return saveSkillRanking(skillRanking, user);
       }).map(savedRanking -> {
           return new ResponseEntity<SkillRanking>(savedRanking, HttpStatus.CREATED); 
       }).orElseThrow(() -> {
           return new AccessDeniedException("You are not authorized to access the page.");
       }); 
   }

   
   /**
    * PUT /skillRanking/{id} -> update a skill Ranking
    */
   @RequestMapping(value = "/skillRanking/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
   @Timed
   @ResponseBody
   public ResponseEntity<SkillRanking> updateSkillRanking(@PathVariable Long id, @RequestBody SkillRanking skillRanking, BindingResult result) {
       log.debug("REST request to update a skill ranking : {}", skillRanking);
       return userService.getUserFromLogin().map(user -> {
           return saveSkillRanking(skillRanking, user);
       }).map(updatedRanking -> {
           return new ResponseEntity<SkillRanking>(updatedRanking, HttpStatus.OK); 
       }).orElseThrow(() -> {
           return new AccessDeniedException("You are not authorized to access the page.");
       }); 
   }
   
    private SkillRanking saveSkillRanking(SkillRanking skillRanking, User user) {
        skillRanking.setUser(user);
        skillRanking.setDateTime((new org.joda.time.LocalDateTime(DateTimeZone.UTC)));
        return save(skillRanking);
    }
    
    /**
     * Write a new record to the skill ranking history table every time we update or save a skill ranking.
     * @param skillRanking
     * @return
     */
    private SkillRanking save(SkillRanking skillRanking) {
        SkillRankingHistory hist = new SkillRankingHistory(skillRanking);
        skillRankingHistoryRepository.save(hist);
        return skillRankingRepository.save(skillRanking);
        
    }
  
}
