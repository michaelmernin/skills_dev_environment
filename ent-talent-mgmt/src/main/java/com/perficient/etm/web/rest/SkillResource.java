package com.perficient.etm.web.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.perficient.etm.domain.Skill;
import com.perficient.etm.domain.SkillCategory;
import com.perficient.etm.domain.SkillRanking;
import com.perficient.etm.domain.SkillRankingHistory;
import com.perficient.etm.repository.SkillRankingHistoryRepository;
import com.perficient.etm.repository.SkillRankingRepository;
import com.perficient.etm.repository.SkillRepository;
import com.perficient.etm.security.AuthoritiesConstants;

/**
 * REST controller for managing Skills of a User
 */
@RestController
@RequestMapping("/api")
public class SkillResource implements RestResource{
    
    private final Logger log = LoggerFactory.getLogger(SkillResource.class);
        
    @Inject
    private SkillRepository skillRepository;
    
    @Inject
    private SkillRankingRepository skillRankingRepository;
    
    @Inject
    private SkillRankingHistoryRepository skillRankingHistoryRepository;
    
    /**
     * GET /skills -> get list of skills
     * @return
     */
    @RequestMapping(value = "/skills", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Skill> getAllSkills() {
        log.debug("REST request to get list of all skills");
        List<Skill> skills = new ArrayList<Skill>();
        skills = skillRepository.findAll();
        return skills;
    }
    
    /**
     * DELETE  /skills/:id -> delete the "id" skill.
     */
    @RequestMapping(value = "/skills/{id}", method = RequestMethod.DELETE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Skill : {}", id);
        List<SkillRanking> skillRanking = skillRankingRepository.findBySkillId(id);
        skillRankingRepository.delete(skillRanking);
        List<SkillRankingHistory> skillRankingHistory = skillRankingHistoryRepository.findBySkillId(id);
        skillRankingHistoryRepository.delete(skillRankingHistory);
        Skill skill = skillRepository.findOne(id);
        skillRepository.delete(skill);
    }
    
    /**
     * POST /skills -> create a skill
     */
    @RequestMapping(value = "/skills", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseBody
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Skill> saveSkillCategory(@RequestBody Skill skill,  BindingResult result) {
        log.debug("REST request to create a skill : {}", skill);
        Skill savedSkill = skillRepository.save(skill);
        return new ResponseEntity<Skill>(savedSkill, HttpStatus.CREATED); 
    }
    
    /**
     * PUT /skills/{id} -> update a skill
     */
    @RequestMapping(value = "/skills/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseBody
    public ResponseEntity<Skill> updateSkill(@PathVariable Long id, @RequestBody Skill skill, BindingResult result) {
       log.debug("REST request to update a skill ranking : {}", skill);
       Skill updatedSkill = skillRepository.save(skill);
       return new ResponseEntity<Skill>(updatedSkill, HttpStatus.OK); 
    }
}
