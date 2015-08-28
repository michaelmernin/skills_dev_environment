package com.perficient.etm.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.perficient.etm.domain.User;
import com.perficient.etm.security.UserDetailsService;

/**
 * Utility class for configuring security context for tests.
 */
public class AuthenticationTestExecutionListener extends DependencyInjectionTestExecutionListener {
    
    Authentication defaultAuthentication;

    public static void setCurrentUser(User user) {
        SecurityContextHolder.getContext().setAuthentication(getAuthentication(user));
    }

    private static Authentication getAuthentication(User user) {
        UserDetails userDetails = UserDetailsService.mapUserDetails(user);
        return new TestingAuthenticationToken(userDetails, user.getLogin(), getAuthorities(userDetails));
    }

    private static List<GrantedAuthority> getAuthorities(UserDetails userDetails) {
        return userDetails.getAuthorities().stream().collect(Collectors.toList());
    }
    
    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        defaultAuthentication = SecurityContextHolder.getContext().getAuthentication();
    }
    
    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        SecurityContextHolder.getContext().setAuthentication(defaultAuthentication);
    }
}
