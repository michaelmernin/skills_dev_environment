package com.perficient.etm.web.rest.dto;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.joda.time.LocalDate;

import com.perficient.etm.domain.Authority;
import com.perficient.etm.domain.User;

public class UserDTO {
    
    @NotNull
    private Long id;

    @Pattern(regexp = "^[a-z0-9]*$")
    @NotNull
    @Size(min = 1, max = 50)
    private String login;

    @NotNull
    @Size(min = 5, max = 100)
    private String password;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Email
    @Size(min = 5, max = 100)
    private String email;

    @Size(min = 2, max = 5)
    private String langKey;

    private List<String> roles;
    
    @Size(max = 50)
    private String title;
    
    @Size(max = 50)
    private String targetTitle;
    
    @Size(max = 50)
    private LocalDate startDate;
    
    @Size(max = 50)
    private User counselor;

    public UserDTO() {
    }
    
    public UserDTO(User user) {
    	this.id = user.getId();
    	this.login = user.getLogin();
    	this.password = null;
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.langKey = user.getLangKey();
        this.title = user.getTitle();
        this.targetTitle = user.getTargetTitle();
        this.startDate = user.getStartDate();
        this.roles = user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toList());
    }
    
    public Long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getLogin() {
        return login;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getLangKey() {
        return langKey;
    }

    public List<String> getRoles() {
        return roles;
    }

    public String getTitle() {
		return title;
	}

	public String getTargetTitle() {
		return targetTitle;
	}

	public LocalDate getStartDate() {
		return startDate;
	}
	
	public User getCounselor() {
		return counselor;
	}

	@Override
    public String toString() {
        return "UserDTO{" +
        "login='" + login + '\'' +
        ", password='" + password + '\'' +
        ", firstName='" + firstName + '\'' +
        ", lastName='" + lastName + '\'' +
        ", email='" + email + '\'' +
        ", langKey='" + langKey + '\'' +
        ", roles=" + roles +
        '}';
    }
}
