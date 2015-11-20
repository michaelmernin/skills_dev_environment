package com.perficient.etm.web.rest;

import java.util.List;
import java.util.Optional;
import com.perficient.etm.domain.User;
import com.perficient.etm.repository.UserRepository;
import com.perficient.etm.utils.ResourceTestUtils;
import com.perficient.etm.utils.SpringAppTest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
public class UserResourceTest extends SpringAppTest {

    private static final String DEFAULT_LOGIN = "dev.user1";
    private static final String UPDATED_LOGIN = "test.user1";
    private static final String UPDATED_FIRST_NAME = "Test";
    private static final String UPDATED_LAST_NAME = "User1";
    private static final String UPDATED_EMAIL = "test.user1@email.com";

    @Inject
    private UserRepository userRepository;
    
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
        int count = (int) userRepository.count() - 2; // exclude System, Anonymous users from count

        // Get all the users
        ResultActions result = restUserMockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].login").value(DEFAULT_LOGIN))
                .andExpect(jsonPath("$[1].id").value(4))
                .andExpect(jsonPath("$[1].login").value("dev.user2"))
                .andExpect(jsonPath("$[2].id").value(5))
                .andExpect(jsonPath("$[2].login").value("dev.user3"))
                .andExpect(jsonPath("$[3].id").value(6))
                .andExpect(jsonPath("$[3].login").value("dev.user4"));
        
        ResourceTestUtils.assertJsonCount(result, count);
        ResourceTestUtils.assertJsonArrayItemKeys(result, count, ResourceTestUtils.PRIVATE_USER_FIELDS);
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
    public void testUpdateUser() throws Exception {
        int count = (int) userRepository.count();
        
        // Update the user
        user.setLogin(UPDATED_LOGIN);
        user.setFirstName(UPDATED_FIRST_NAME);
        user.setLastName(UPDATED_LAST_NAME);
        user.setEmail(UPDATED_EMAIL);
        restUserMockMvc.perform(put("/api/users/" + user.getId())
                .contentType(ResourceTestUtils.APPLICATION_JSON_UTF8)
                .content(ResourceTestUtils.convertObjectToJsonBytes(user, objectMapper)))
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

    @Test
    @WithUserDetails("dev.user3")
    public void testGetUserCounselees() throws Exception {
        // Get the counselees for the authenticated counselor
        ResultActions result = restUserMockMvc.perform(get("/api/counselees")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].login").value(DEFAULT_LOGIN));


        ResourceTestUtils.assertJsonCount(result, 3);
        ResourceTestUtils.assertJsonArrayItemKeys(result, 3, ResourceTestUtils.COUNSELEE_USER_FIELDS);
    }
}
