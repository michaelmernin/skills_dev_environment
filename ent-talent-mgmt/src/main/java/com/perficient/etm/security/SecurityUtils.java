package com.perficient.etm.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.perficient.etm.domain.User;
import com.perficient.etm.repository.UserRepository;

import java.util.Collection;
import java.util.Optional;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

    public static final String SYSTEM_USERNAME = "system";

    private SecurityUtils() {
    }

    /**
     * Get the current user.
     */
    public static Optional<UserDetails> getPrincipal() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        UserDetails springSecurityUser = null;
        if(authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            springSecurityUser = (UserDetails) authentication.getPrincipal();
        }
        return Optional.ofNullable(springSecurityUser);
    }

    /**
     * Get the details of the current user.
     */
    public static Optional<AppUserDetails> getAppUserDetails() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        AppUserDetails appUserDetails = null;
        if(authentication != null && authentication.getPrincipal() instanceof AppUserDetails) {
            appUserDetails = (AppUserDetails) authentication.getPrincipal();
        }
        return Optional.ofNullable(appUserDetails);
    }

    /**
     * Get the login of the current user.
     */
    public static String getCurrentLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        UserDetails springSecurityUser = null;
        String userName = null;
        if(authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                springSecurityUser = (UserDetails) authentication.getPrincipal();
                userName = springSecurityUser.getUsername();
            } else if (authentication.getPrincipal() instanceof String) {
                userName = (String) authentication.getPrincipal();
            }
        }
        return userName;
    }

    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Collection<? extends GrantedAuthority> authorities = securityContext.getAuthentication().getAuthorities();
        if (authorities != null) {
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals(AuthoritiesConstants.ANONYMOUS)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Check if a user has a role.
     *
     * @return true if the user has the authority, false otherwise
     */
    public static boolean hasRole(User user, String role) {
        return user.getAuthorities().stream().anyMatch(a -> {
            return a.getName().equals(role);
        });
    }

    public static void runAs(UserDetails user, Runnable function) {
        SecurityContext security = SecurityContextHolder.getContext();
        Authentication currentAuth = security.getAuthentication();
        security.setAuthentication(new PreAuthenticatedAuthenticationToken(user, user.getUsername(), user.getAuthorities()));
        function.run();
        security.setAuthentication(currentAuth);
    }
    
    public static void runAsSystem(UserRepository userRepository, Runnable function) {
        userRepository.findOneByLogin(SYSTEM_USERNAME)
        .map(UserDetailsService::mapUserDetails)
        .ifPresent(systemUser -> {
            SecurityUtils.runAs(systemUser, function);
        });
    }
}
