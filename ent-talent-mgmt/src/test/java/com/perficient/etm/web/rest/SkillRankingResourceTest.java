package com.perficient.etm.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perficient.etm.domain.Skill;
import com.perficient.etm.domain.SkillRanking;
import com.perficient.etm.repository.SkillRankingHistoryRepository;
import com.perficient.etm.repository.SkillRankingRepository;
import com.perficient.etm.repository.SkillRepository;
import com.perficient.etm.service.UserService;
import com.perficient.etm.utils.ResourceTestUtils;
import com.perficient.etm.utils.SpringAppTest;

/**
 * Test class for the SkillRankingResource REST controller.
 *
 * @see SkillRankingResource
 */
public class SkillRankingResourceTest extends SpringAppTest {

    private static final Integer DEFAULT_RANK = 4;
    private static final Long DEFAULT_SKILL_ID = 4L;
    private static final Long DEFAULT_RANKING_ID = 1L;

    @Inject
    private SkillRankingRepository skillRankingRepository;

    @Inject
    private SkillRankingHistoryRepository skillRankingHistoryRepository;

    @Inject
    private SkillRepository skillRepository;

    @Inject
    private UserService userService;

    private MockMvc restSkillRankingMockMvc;

    private SkillRanking ranking;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SkillRankingResource skillRankingResource = new SkillRankingResource();
        ReflectionTestUtils.setField(skillRankingResource, "skillRankingRepository", skillRankingRepository);
        ReflectionTestUtils.setField(skillRankingResource, "skillRankingRepository", skillRankingRepository);
        ReflectionTestUtils.setField(skillRankingResource, "skillRankingHistoryRepository", skillRankingHistoryRepository);
        ReflectionTestUtils.setField(skillRankingResource, "userService", userService);
        this.restSkillRankingMockMvc = MockMvcBuilders.standaloneSetup(skillRankingResource).build();
    }

    @Before
    public void initTest() {
        ranking= new SkillRanking();
        Skill skill = skillRepository.findOne(DEFAULT_SKILL_ID);
        ranking.setRank(DEFAULT_RANK);
        ranking.setSkill(skill);
    }

    @Test
    @Transactional
    @WithUserDetails("dev.user1")
    public void saveSkillRanking() throws Exception {
        int count = (int) skillRankingRepository.count();
        LocalDateTime ldt = new LocalDateTime(DateTimeZone.UTC);
        // Create the Ranking
        restSkillRankingMockMvc.perform(post("/api/skillRanking")
                .contentType(ResourceTestUtils.APPLICATION_JSON_UTF8)
                .content(skillRankingAsJson(ranking)))
        .andExpect(status().isCreated());

        // Validate the Ranking in the database
        List<SkillRanking> skillRankings = skillRankingRepository.findAll();
        assertThat(skillRankings).hasSize(count + 1);
        SkillRanking testRanking = skillRankings.get(skillRankings.size() - 1);
        assertThat(testRanking.getRank()).isEqualTo(DEFAULT_RANK);
        assertThat(testRanking.getSkill().getId()).isEqualTo(DEFAULT_SKILL_ID);
        assertThat(testRanking.getDateTime().getMillisOfDay()).isGreaterThan(ldt.getMillisOfDay());
        assertThat(testRanking.getUser()).isEqualTo(userService.getUserFromLogin().get());
    }

   /* @Test
    @Transactional
    @WithUserDetails("dev.user1")
    public void updateSkillRanking() throws Exception {
        SkillRanking skillRanking = skillRankingRepository.findOne(DEFAULT_RANKING_ID);
        int rank = skillRanking.getRank();
        skillRanking.setDateTime(null);
        if (skillRanking.getRank() != 5) {
            skillRanking.setRank(skillRanking.getRank() + 1);
        } else {
            skillRanking.setRank(1);
        }
        // Update the Ranking
        restSkillRankingMockMvc.perform(put("/api/skillRanking/{id}", DEFAULT_RANKING_ID)
                .contentType(ResourceTestUtils.APPLICATION_JSON_UTF8)
                .content(skillRankingAsJson(skillRanking)))
        .andExpect(status().isOk());

        // Validate the Ranking in the database
        skillRanking = skillRankingRepository.findOne(DEFAULT_RANKING_ID);
        assertThat(skillRanking.getRank() != rank);
        assertThat(skillRanking.getRank() == rank + 1 || skillRanking.getRank() == 1);
    }*/

    private String skillRankingAsJson(SkillRanking skillRanking) {
        String s = "";
        try {
            s = getJson(skillRanking);
        } catch (Exception e) {
        }
        s = s.substring(0, s.length() - 1);
        s = s + ",\"skill\":{\"id\":" + skillRanking.getSkill().getId() + "}}";
        return s;
    }

    private String getJson(SkillRanking skillRanking) throws JsonProcessingException {
        String s;
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        s = mapper.writeValueAsString(skillRanking);
        return s;
    }

}
