package com.perficient.etm.service;

import com.perficient.etm.domain.PersistentToken;
import com.perficient.etm.domain.User;
import com.perficient.etm.repository.PersistentTokenRepository;
import com.perficient.etm.repository.UserRepository;
import com.perficient.etm.utils.SpringAppTest;

import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Test;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.*;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserService
 */
public class UserServiceTest extends SpringAppTest {

    @Inject
    private PersistentTokenRepository persistentTokenRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Test
    @Ignore
    public void testRemoveOldPersistentTokens() {
        User admin = userRepository.findOneByLogin("dev.user1").get();
        int existingCount = persistentTokenRepository.findByUser(admin).size();
        generateUserToken(admin, "1111-1111", new LocalDate());
        LocalDate now = new LocalDate();
        generateUserToken(admin, "2222-2222", now.minusDays(32));
        assertThat(persistentTokenRepository.findByUser(admin)).hasSize(existingCount + 2);
        userService.removeOldPersistentTokens();
        assertThat(persistentTokenRepository.findByUser(admin)).hasSize(existingCount + 1);
    }

    private void generateUserToken(User user, String tokenSeries, LocalDate localDate) {
        PersistentToken token = new PersistentToken();
        token.setSeries(tokenSeries);
        token.setUser(user);
        token.setTokenValue(tokenSeries + "-data");
        token.setTokenDate(localDate);
        token.setIpAddress("127.0.0.1");
        token.setUserAgent("Test agent");
        persistentTokenRepository.saveAndFlush(token);
    }
}
