package com.perficient.etm.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface AppUserDetails extends UserDetails {

    public String getFirstName();
    
    public String getLastName();
    
    public String getEmail();
    
    public String getEmployeeId();

	public String getTitle();
}
