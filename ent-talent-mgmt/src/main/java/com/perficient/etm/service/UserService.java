package com.perficient.etm.service;

import com.perficient.etm.domain.Authority;
import com.perficient.etm.domain.User;
import com.perficient.etm.repository.AuthorityRepository;
import com.perficient.etm.repository.PersistentTokenRepository;
import com.perficient.etm.repository.UserRepository;
import com.perficient.etm.security.AppUserDetails;
import com.perficient.etm.security.AuthoritiesConstants;
import com.perficient.etm.security.SecurityUtils;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import java.util.HashSet;
import java.util.Locale;
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

    public void updateUserInformation(String firstName, String lastName, String email) {
        userRepository.findOneByLogin(SecurityUtils.getCurrentLogin()).ifPresent(u -> {
            u.setFirstName(firstName);
            u.setLastName(lastName);
            u.setEmail(email);
            userRepository.save(u);
            log.debug("Changed Information for User: {}", u);
        });
    }

    public User getUser(Long userId) {
    	return userRepository.getOne(userId);
    }
    
    public User getUserWithAuthorities() {
        return userRepository.findOneByLogin(SecurityUtils.getCurrentLogin())
            .orElseGet(() -> {
                return this.getFromAppUserDetails().orElse(null);
            });
    }
    
    public Optional<User> getFromAppUserDetails() {
        return SecurityUtils.getAppUserDetails().map(ud -> {
            return userRepository.findOneByEmployeeId(ud.getEmployeeId().toLowerCase())
                .map(u -> {
                    setUserDetails(u, ud);
                    return userRepository.save(u);
                }).orElseGet(() -> {
                    return createUser(ud);
                });
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
    
    private User createUser(AppUserDetails userDetails) {
        return createUser(userDetails, Locale.US.toLanguageTag());
    }

    private User createUser(AppUserDetails userDetails, String langKey) {
        User newUser = new User();
        setUserDetails(newUser, userDetails);
        newUser.setLangKey(langKey);
        setUserAuthorities(newUser);
        userRepository.saveAndFlush(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    private void setUserAuthorities(User newUser) {
        Set<Authority> authorities = new HashSet<>();
        authorities.add(authorityRepository.findOne(AuthoritiesConstants.USER));
        if (userRepository.count() < 3) { // if first user except for system and anonymousUser
            authorities.add(authorityRepository.findOne(AuthoritiesConstants.ADMIN));
        }

        newUser.setAuthorities(authorities);
    }
    
    private void setUserDetails(User user, AppUserDetails userDetails) {
        user.setLogin(userDetails.getUsername().toLowerCase());
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setEmail(userDetails.getEmail());
        user.setEmployeeId(userDetails.getEmployeeId().toLowerCase());
        user.setTitle(userDetails.getTitle());
    }

}
