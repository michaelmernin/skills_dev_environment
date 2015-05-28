package com.perficient.etm.security;

import com.perficient.etm.domain.User;
import com.perficient.etm.repository.UserRepository;
import com.perficient.etm.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import java.util.stream.Collectors;
import java.util.List;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(UserDetailsService.class);

    @Inject
    private UserRepository userRepository;
    
    @Inject
    private UserService userService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);
        return userRepository.findOneByLogin(login.toLowerCase())
            .map(this::mapUserDetails)
            .orElseGet(() -> {
                return userService.createFromAppUserDetails()
                    .map(this::mapUserDetails)
                    .orElseThrow(() -> {
                        return userNotFoundException(login);
                    });
            });
    }
    
    private AuthenticationException userNotFoundException(String login) {
        return new UsernameNotFoundException("Unable to locate user with login: " + login);
    }

    private UserDetails mapUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(user.getLogin().toLowerCase(),
                "NotNull", getGrantedAuthorities(user));
    }

    public static List<GrantedAuthority> getGrantedAuthorities(User user) {
        return user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toList());
    }
}
