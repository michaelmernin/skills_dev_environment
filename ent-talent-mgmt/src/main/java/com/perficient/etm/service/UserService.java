package com.perficient.etm.service;

import com.perficient.etm.domain.Authority;
import com.perficient.etm.domain.User;
import com.perficient.etm.repository.AuthorityRepository;
import com.perficient.etm.repository.PersistentTokenRepository;
import com.perficient.etm.repository.UserRepository;
import com.perficient.etm.security.SecurityUtils;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Inject
    private UserRepository userRepository;

    @Inject
    private PersistentTokenRepository persistentTokenRepository;

    @Inject
    private AuthorityRepository authorityRepository;
    
    private User createUserInformation(String login, String firstName, String lastName, String email) {
    	return createUserInformation(login, firstName, lastName, email, "en");
    }

    private User createUserInformation(String login, String firstName, String lastName, String email,
                                      String langKey) {
        User newUser = new User();
        Authority authority = authorityRepository.findOne("ROLE_USER");
        Set<Authority> authorities = new HashSet<>();
        newUser.setLogin(login);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setLangKey(langKey);
        authorities.add(authority);
        newUser.setAuthorities(authorities);
        userRepository.saveAndFlush(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public void updateUserInformation(String firstName, String lastName, String email) {
        userRepository.findOneByLogin(SecurityUtils.getCurrentLogin()).ifPresent(u -> {
            u.setFirstName(firstName);
            u.setLastName(lastName);
            u.setEmail(email);
            userRepository.save(u);
            log.debug("Changed Information for User: {}", u);
        });
    }

    private User eagerLoad(User user) {
        user.getAuthorities().size();
        return user;
    }

    public User getUserWithAuthorities() {
        return (User) userRepository.findOneByLogin(SecurityUtils.getCurrentLogin())
            .map(this::eagerLoad)
            .orElseGet(() -> {
                return this.createFromAppUserDetails().map(this::eagerLoad).orElse(null);
            });
    }
    
    public Optional<User> createFromAppUserDetails() {
        return SecurityUtils.getAppUserDetails().map(ud -> {
            return createUserInformation(ud.getUsername(), ud.getFirstName(), ud.getLastName(), ud.getEmail());
        });
    }

    /**
     * Persistent Token are used for providing automatic authentication, they should be automatically deleted after
     * 30 days.
     * <p/>
     * <p>
     * This is scheduled to get fired everyday, at midnight.
     * </p>
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void removeOldPersistentTokens() {
        LocalDate now = new LocalDate();
        persistentTokenRepository.findByTokenDateBefore(now.minusMonths(1)).stream().forEach(token ->{
            log.debug("Deleting token {}", token.getSeries());
            User user = token.getUser();
            user.getPersistentTokens().remove(token);
            persistentTokenRepository.delete(token);
        });
    }
}