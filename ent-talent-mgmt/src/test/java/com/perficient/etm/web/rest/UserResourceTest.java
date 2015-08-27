package com.perficient.etm.web.rest;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.perficient.etm.Application;
import com.perficient.etm.domain.User;
import com.perficient.etm.repository.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
@IntegrationTest
public class UserResourceTest {

    private static final String DEFAULT_LOGIN = "dev.user1";
    private static final String UPDATED_LOGIN = "test.user1";
    private static final String UPDATED_FIRST_NAME = "Test";
    private static final String UPDATED_LAST_NAME = "User1";
    private static final String UPDATED_EMAIL = "test.user1@email.com";

    @Inject
    private UserRepository userRepository;
    
    @Inject
    private ObjectMapper objectMapper;

    private MockMvc restUserMockMvc;
    
    private User user;

    @PostConstruct
    public void setup() {
        UserResource userResource = new UserResource();
        ReflectionTestUtils.setField(userResource, "userRepository", userRepository);
        this.restUserMockMvc = MockMvcBuilders.standaloneSetup(userResource).build();
    }
    
    @Before
    public void initTest() {
        userRepository.findOneByLogin(DEFAULT_LOGIN).ifPresent(u -> {
            user = u;
        });
    }
    
    @Test
    public void getAllUsers() throws Exception {
        // Get all the users
        restUserMockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].login").value(DEFAULT_LOGIN))
                .andExpect(jsonPath("$[1]").exists())
                .andExpect(jsonPath("$[1].id").value(4))
                .andExpect(jsonPath("$[1].login").value("dev.user2"))
                .andExpect(jsonPath("$[2]").exists())
                .andExpect(jsonPath("$[2].id").value(5))
                .andExpect(jsonPath("$[2].login").value("dev.user3"))
                .andExpect(jsonPath("$[3]").exists())
                .andExpect(jsonPath("$[3].id").value(6))
                .andExpect(jsonPath("$[3].login").value("dev.user4"));
    }

    @Test
    public void testGetExistingUser() throws Exception {
        restUserMockMvc.perform(get("/api/users/{login}", DEFAULT_LOGIN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.lastName").value("UserOne"));
    }

    @Test
    public void testGetUnknownUser() throws Exception {
        restUserMockMvc.perform(get("/api/users/unknown")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @Transactional
    public void testUpdateUser() throws Exception {
        int count = (int) userRepository.count();
        
        // Update the user
        user.setLogin(UPDATED_LOGIN);
        user.setFirstName(UPDATED_FIRST_NAME);
        user.setLastName(UPDATED_LAST_NAME);
        user.setEmail(UPDATED_EMAIL);
        restUserMockMvc.perform(put("/api/users/" + user.getId())
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user, objectMapper)))
                .andExpect(status().isOk());
        
        // Validate the User in the database
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(count);
        Optional<User> optional = users.stream().filter(u -> {return u.getId() == user.getId();}).findAny();
        assertThat(optional.isPresent()).isTrue();
        User testUser = optional.get();
        assertThat(testUser.getLogin()).isEqualTo(UPDATED_LOGIN);
        assertThat(testUser.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testUser.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testUser.getEmail()).isEqualTo(UPDATED_EMAIL);
    }
}
