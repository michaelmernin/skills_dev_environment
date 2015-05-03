package com.perficient.etm.security.ldap;

import java.util.Collection;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;

public class CustomLdapUserDetailsMapper extends LdapUserDetailsMapper {

    private static final String COMMON_NAME = "cn";

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
        LdapUserDetails ldapUserDetails = (LdapUserDetails) super.mapUserFromContext(ctx, username, authorities);

        try {
            LdapName dn = new LdapName(ldapUserDetails.getDn());

            boolean hasCn = dn.getRdns().stream().anyMatch(rdn -> {
                return COMMON_NAME.equals(rdn.getType());
            });

            if (!hasCn) {
                String fullName = ctx.getStringAttribute(COMMON_NAME);
                dn.add(new Rdn(COMMON_NAME, fullName));
            }

        } catch (InvalidNameException e) {}

        return ldapUserDetails;
    }
}
