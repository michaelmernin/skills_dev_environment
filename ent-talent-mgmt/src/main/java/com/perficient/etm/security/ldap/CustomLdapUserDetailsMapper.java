package com.perficient.etm.security.ldap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.ppolicy.PasswordPolicyControl;
import org.springframework.security.ldap.ppolicy.PasswordPolicyResponseControl;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.stereotype.Component;

import com.perficient.etm.repository.UserAuthorityRepository;
import com.perficient.etm.repository.UserRepository;
import com.perficient.etm.security.AppUserDetails;
import com.perficient.etm.security.AppUserDetailsImpl;
import com.perficient.etm.security.AuthoritiesConstants;

@Component
public class CustomLdapUserDetailsMapper extends LdapUserDetailsMapper {

    private final Log logger = LogFactory.getLog(CustomLdapUserDetailsMapper.class);

    private String passwordAttributeName = "userPassword";

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserAuthorityRepository userAuthorityRepository;

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
        AppUserDetailsImpl.Essence essence = getEssence(ctx, username, authorities);

        userRepository.findOneByLogin(username).map(u -> {
            return userAuthorityRepository.findAllByUserId(u.getId()).stream().map(ua -> {
                return new SimpleGrantedAuthority(ua.getAuthorityName());
            }).collect(Collectors.toList());
        }).orElseGet(() -> {
            List<SimpleGrantedAuthority> defaultRoles = new ArrayList<>();
            defaultRoles.add(new SimpleGrantedAuthority(AuthoritiesConstants.USER));
            return defaultRoles;
        }).forEach(ga -> {
            essence.addAuthority(ga);
        });

        AppUserDetails ldapUserDetails = essence.createAppUserDetails();

        return ldapUserDetails;
    }

    private AppUserDetailsImpl.Essence getEssence(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
        logger.debug("Mapping user details from context with DN: " + ctx.getDn());

        AppUserDetailsImpl.Essence essence = new AppUserDetailsImpl.Essence(ctx);

        Object passwordValue = ctx.getObjectAttribute(passwordAttributeName);

        if (passwordValue != null) {
            essence.setPassword(mapPassword(passwordValue));
        }

        essence.setUsername(username);

        // Add the supplied authorities

        for (GrantedAuthority authority : authorities) {
            essence.addAuthority(authority);
        }

        // Check for PPolicy data

        PasswordPolicyResponseControl ppolicy = (PasswordPolicyResponseControl) ctx.getObjectAttribute(PasswordPolicyControl.OID);

        if (ppolicy != null) {
            essence.setTimeBeforeExpiration(ppolicy.getTimeBeforeExpiration());
            essence.setGraceLoginsRemaining(ppolicy.getGraceLoginsRemaining());
        }

        return essence;
    }
}
