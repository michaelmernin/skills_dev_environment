package com.perficient.etm.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.perficient.etm.domain.Skill;
import com.perficient.etm.domain.SkillCategory;
import com.perficient.etm.repository.SkillCategoryRepository;
import com.perficient.etm.repository.SkillRankingRepository;
import com.perficient.etm.repository.SkillRepository;
import com.perficient.etm.service.UserService;
import com.perficient.etm.utils.ResourceTestUtils;
import com.perficient.etm.utils.SpringAppTest;

/**
 * Test class for the SkillCategoryResource REST controller.
 *
 * @see SkillCategoryResource
 */
public class SkillCategoryResourceTest extends SpringAppTest {

    private static final Long DEFAULT_SKILLCATEGORY_ID = 1L;
    private static final Long DEFAULT_SKILL_ID = 1L;

    @Inject
    private SkillCategoryRepository skillCategoryRepository;

    @Inject
    private SkillRepository skillRepository;

    @Inject
    private SkillRankingRepository skillRankingRepository;

    @Inject
    private UserService userService;

    private MockMvc restSkillCategoryMockMvc;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SkillCategoryResource skillCategoryResource = new SkillCategoryResource();
        ReflectionTestUtils.setField(skillCategoryResource, "skillCategoryRepository", skillCategoryRepository);
        ReflectionTestUtils.setField(skillCategoryResource, "skillRankingRepository", skillRankingRepository);
        ReflectionTestUtils.setField(skillCategoryResource, "userService", userService);
        this.restSkillCategoryMockMvc = MockMvcBuilders.standaloneSetup(skillCategoryResource).build();
    }

    @Before
    public void initTest() {
    }

    @Test
    @Ignore
    @Transactional
    @WithUserDetails("dev.user1")
    public void getAllSkillsForUser() throws Exception {
        // Read a category
        SkillCategory skillCategory = skillCategoryRepository.findOne(DEFAULT_SKILLCATEGORY_ID);
        Skill skill = skillRepository.findOne(DEFAULT_SKILL_ID); 
        int scCount = new Long(skillCategoryRepository.count()).intValue();
        int skillCount = skillCategory.getSkills().size();

        // Get all the categories
        ResultActions resultActions = restSkillCategoryMockMvc.perform(get("/api/skillCategories"));
        resultActions.andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.[0].id").value(skillCategory.getId().intValue()))
        .andExpect(jsonPath("$.[0].title").value(skillCategory.getTitle()))
        .andExpect(jsonPath("$.[0].skills[0].id").value(skill.getId().intValue()))
        .andExpect(jsonPath("$.[0].skills[0].name").value(skill.getName()));

        ResourceTestUtils.assertJsonArrayItemKeys(resultActions, scCount, "id", "title", "skills");
        ResourceTestUtils.assertJsonArrayItemKeys(resultActions, "$.[0].skills", skillCount, "id", "name", "rankings");
        for (int i = 0; i < skillCount; i++) {
            String path = "$.[0].skills["+i+"].rankings";
            ResourceTestUtils.assertJsonArrayItemKeys(resultActions, path, 1, "id", "rank");
            resultActions.andExpect(jsonPath(path, Matchers.hasSize(1)));
        }
    }

}
