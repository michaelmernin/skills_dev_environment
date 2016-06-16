package com.perficient.etm.web.rest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

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
import com.perficient.etm.domain.Skill;
import com.perficient.etm.domain.SkillCategory;
import com.perficient.etm.domain.SkillRanking;
import com.perficient.etm.domain.User;
import com.perficient.etm.repository.SkillCategoryRepository;
import com.perficient.etm.repository.SkillRankingRepository;
import com.perficient.etm.repository.UserRepository;
import com.perficient.etm.service.UserService;


/**
 * REST controller for managing SkillCategory
 */

@RestController
@RequestMapping("/api")
public class SkillCategoryResource implements RestResource{
    private final Logger log = LoggerFactory.getLogger(SkillCategoryResource.class);
    
    @Inject
    private SkillCategoryRepository skillCategoryRepository;
    
    @Inject
    private SkillRankingRepository skillRankingRepository;
    
    @Inject
    private UserRepository userRepository;
    
    @Inject
    private UserService userService;
    
    
    /**
     * GET /skillCategories -> get list of skill Categories based on user login and enabled flag
     * @return
     */
    @RequestMapping(value = "/skillCategories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SkillCategory> getAllSkillsForUser() {
        log.debug("REST request to get a list of skill Categories");
        return userService.getUserFromLogin().map(user -> {
            List<SkillCategory> categories = skillCategoryRepository.findByEnabled(true);
            List<SkillRanking> rankings = skillRankingRepository.findByUserId(user.getId());
            final Map<Long,Skill> skills = new HashMap<Long,Skill>();
            
            categories.forEach(sc ->{
                List<Skill> tempSkills = new ArrayList<Skill>();
                tempSkills.addAll(sc.getSkills());
                tempSkills.forEach(skill ->{
                    if(skill.getEnabled()){
                        skill.setRankings(new ArrayList<SkillRanking>());
                        skill.getRankings().add(new SkillRanking(user, skill));
                        skills.put(skill.getId(), skill);
                    }else {
                        sc.getSkills().remove(skill);
                    }
                });
            });
           
            rankings.forEach(rank ->{
                Skill skill = skills.get(rank.getSkill().getId());
                if(skill != null){
                    skill.getRankings().set(0, rank);
                }
            });
            
            return categories;
        }).orElse(new ArrayList<>());
    }
    
    /**
     * GET /skillCategories/all -> get list of all skill Categories including enabled/disabled
     * @return
     */
    @RequestMapping(value = "/skillCategories/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SkillCategory> getAllSkillCategories() {
        log.debug("REST request to get a list of skill Categories");
        List<SkillCategory> categories = skillCategoryRepository.findAll();
        return categories;
    }
    
    /**
     * GET /skillCategories/skillsReview -> get list of all skill Categories for all users
     * @return
     */
    @RequestMapping(value = "/skillCategories/skillsReview", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SkillCategory> getAllSkillCategoriesForAllUsers() {
        log.debug("REST request to get a list of skill Categories for all users");
        List<User> users = new ArrayList<User>();
        User loggedUser = userService.getUserFromLogin().get();
            if(loggedUser.isDirector() || loggedUser.isGeneralManager()){
                users = userRepository.findAll();
            }else if(loggedUser.isConselor()){
                users = userRepository.findByCounselor(loggedUser);
            }
        List<User> finalUsers = users;
        List<SkillCategory> categories = skillCategoryRepository.findByEnabled(true);
       
        
        categories.forEach(sc ->{
            List<Skill> tempSkills = sc.getSkills().stream().filter(sk -> sk.getEnabled()).collect(Collectors.toList());
            sc.setSkills(tempSkills);

            sc.getSkills().forEach(skill -> {
                List<SkillRanking> tempSkillRanking = skill.getRankings();
                skill.setRankings(new ArrayList<>());
                finalUsers.stream().forEach(user -> {
                    List<SkillRanking> ranks = tempSkillRanking.stream()
                            .filter(r -> r.getUser().getId() == user.getId()).collect(Collectors.toList());
                    if (ranks.isEmpty()) {
                        skill.getRankings().add(new SkillRanking(user, skill));
                    } else {
                        skill.getRankings().add(ranks.get(0));
                    }
                    ;
                });
                skill.getRankings().sort(SkillRanking.getUserComparator());
            });
        });
        
        return categories;
    }
    
    
    /**
     * POST /skillCategories -> create a skill Category
     */
    @RequestMapping(value = "/skillCategories",method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseBody
    public ResponseEntity<SkillCategory> saveSkillCategory(@RequestBody SkillCategory skillCategory,  BindingResult result) {
        log.debug("REST request to create a skill category : {}", skillCategory);
        SkillCategory savedSkillCategory = skillCategoryRepository.save(skillCategory);
        return new ResponseEntity<SkillCategory>(savedSkillCategory, HttpStatus.CREATED); 
    }
    
    /**
     * PUT /skillCategories/{id} -> update a skill Category
     */
    @RequestMapping(value = "/skillCategories/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseBody
    public ResponseEntity<SkillCategory> updateSkillCategory(@PathVariable Long id, @RequestBody SkillCategory skillCategory, BindingResult result) {
       log.debug("REST request to update a skill ranking : {}", skillCategory);
       SkillCategory updatedSkillCategory = skillCategoryRepository.save(skillCategory);
       return new ResponseEntity<SkillCategory>(updatedSkillCategory, HttpStatus.OK); 
    }

}
