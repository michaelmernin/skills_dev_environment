package com.perficient.etm.security;

import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.ldap.authentication.AbstractLdapAuthenticator;
import org.springframework.stereotype.Component;

@Component
public class LdapAuthenticatorPostProcessor implements ObjectPostProcessor<AbstractLdapAuthenticator> {

    private final String[] USER_ATTRIBUTES = new String[] {"mail", "givenName", "sn", "employeeID", "title"};
    
    @Override
    public <O extends AbstractLdapAuthenticator> O postProcess(O object) {
        ((AbstractLdapAuthenticator) object).setUserAttributes(USER_ATTRIBUTES);
        return object;
    }
}