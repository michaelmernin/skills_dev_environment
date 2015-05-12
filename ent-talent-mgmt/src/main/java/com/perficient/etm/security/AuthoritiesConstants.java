package com.perficient.etm.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    private AuthoritiesConstants() {
    }

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";
    
    public static final String COUNCELOR = "ROLE_COUNCELOR";
    
    public static final String GENERAL_MANAGER = "ROLE_GENERAL_MANAGER";
    
    public static final String USER = "ROLE_USER";
}
