package com.perficient.etm.security;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;

public class AppUserDetailsImpl extends LdapUserDetailsImpl implements AppUserDetails {

    private static final long serialVersionUID = 1323756778220381423L;

    private String firstName;
   
    private String lastName;
   
    private String email;
    
    @Override
    public String getFirstName() {
        return firstName;
    }
    
    @Override
    public String getLastName() {
        return lastName;
    }
    
    @Override
    public String getEmail() {
        return email;
    }
    
    public static class Essence extends LdapUserDetailsImpl.Essence {

        public Essence() {
        }

        public Essence(DirContextOperations ctx) {
            super(ctx);
            setFirstName(ctx.getStringAttribute("givenName"));
            setLastName(ctx.getStringAttribute("sn"));
            setEmail(ctx.getStringAttribute("mail"));
        }

        public Essence(AppUserDetailsImpl copyMe) {
            super(copyMe);
            setFirstName(copyMe.firstName);
            setLastName(copyMe.lastName);
            setEmail(copyMe.email);
        }

        protected LdapUserDetailsImpl createTarget() {
            return new AppUserDetailsImpl();
        }

        public void setLastName(String lastName) {
            ((AppUserDetailsImpl) instance).lastName = lastName;
        }

        public void setFirstName(String firstName) {
            ((AppUserDetailsImpl) instance).firstName = firstName;
        }

        public void setEmail(String email) {
            ((AppUserDetailsImpl) instance).email = email;
        }

        public AppUserDetails createAppUserDetails() {
            return (AppUserDetails) super.createUserDetails();
        }
    }
}
