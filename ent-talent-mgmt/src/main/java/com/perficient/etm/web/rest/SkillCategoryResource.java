package com.perficient.etm.web.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.perficient.etm.domain.Skill;
import com.perficient.etm.domain.SkillCategory;
import com.perficient.etm.domain.SkillRanking;
import com.perficient.etm.repository.SkillCategoryRepository;
import com.perficient.etm.repository.SkillRankingRepository;
import com.perficient.etm.service.UserService;


/**
 * REST controller for managing SkillCategory
 */

@RestController
@RequestMapping("/api")
public class SkillCategoryResource {
    private final Logger log = LoggerFactory.getLogger(SkillCategoryResource.class);
    
    @Inject
    private SkillCategoryRepository skillCategoryRepository;
    
    @Inject
    private SkillRankingRepository skillRankingRepository;
    
    @Inject
    private UserService userService;
    
    
    /**
     * GET /skillCategories -> get list of skill Categories
     * @return
     */
    @RequestMapping(value = "/skillCategories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SkillCategory> getAllSkillsForUser() {
        log.debug("REST request to get a list of skill Categories");
        return userService.getUserFromLogin().map(user -> {
            List<SkillCategory> categories = skillCategoryRepository.findAll();
            List<SkillRanking> rankings = skillRankingRepository.findByUserId(user.getId());
            final Map<Long,Skill> skills = new HashMap<Long,Skill>();
            
            categories.forEach(sc ->{
                sc.getSkills().forEach(skill ->{
                    skill.setRankings(new ArrayList<SkillRanking>());
                    skill.getRankings().add(new SkillRanking(user, skill));
                    skills.put(skill.getId(), skill);
                });
            });
           
            rankings.forEach(rank ->{
                Skill skill = skills.get(rank.getSkill().getId());
                skill.getRankings().set(0, rank);
            });
            
            return categories;
        }).orElse(new ArrayList<>());
    }
}
