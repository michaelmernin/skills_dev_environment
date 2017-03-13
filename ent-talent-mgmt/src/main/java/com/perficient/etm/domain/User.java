package com.perficient.etm.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Email;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.perficient.etm.domain.util.CustomLocalDateSerializer;
import com.perficient.etm.domain.util.ISO8601LocalDateDeserializer;
import com.perficient.etm.domain.util.PublicSerializer;
import com.perficient.etm.security.AuthoritiesConstants;
import com.perficient.etm.security.SecurityUtils;
import com.perficient.etm.web.view.View;

/**
 * A user.
 */
@Entity
@Table(name = "T_USER")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 9112749211348786719L;

    @JsonView(View.Identity.class)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonView(View.Public.class)
    @NotNull
    @Pattern(regexp = "^[a-z0-9\\.]*$")
    @Size(min = 1, max = 50)
    @Column(length = 50, unique = true, nullable = false)
    private String login;

    @JsonView(View.Public.class)
    @Size(max = 50)
    @Column(name = "first_name", length = 50)
    private String firstName;

    @JsonView(View.Public.class)
    @Size(max = 50)
    @Column(name = "last_name", length = 50)
    private String lastName;

    @JsonView(View.Public.class)
    @Email
    @Size(max = 100)
    @Column(length = 100, unique = true)
    private String email;

    @JsonView(View.Private.class)
    @Size(max = 50)
    @Column(name = "employee_id", length = 50)
    private String employeeId;

    @JsonView(View.Private.class)
    @Size(min = 2, max = 5)
    @Column(name = "lang_key", length = 5)
    private String langKey;

    @JsonView(View.Private.class)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "T_USER_AUTHORITY",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "name")})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Authority> authorities = new HashSet<>();

    @OneToMany(mappedBy = "reviewee",fetch = FetchType.EAGER)
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Review> selfReviews;

    @OneToMany(mappedBy = "reviewer")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Review> colleagueReviews;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "T_PEER",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "review_id", referencedColumnName = "id")})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Review> peerReviews = new HashSet<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<PersistentToken> persistentTokens = new HashSet<>();

    @JsonSerialize(using = PublicSerializer.class)
    @JsonView(View.Peer.class)
    @ManyToOne(fetch = FetchType.EAGER)
    private User counselor;

    @JsonView(View.Counselee.class)
    @Column(name = "title")
    private String title;

    @JsonView(View.Counselee.class)
    @Column(name = "target_title")
    private String targetTitle;

    @JsonView(View.Counselee.class)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "start_date")
    private LocalDate startDate;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<SkillRanking> skillRanking;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public User getCounselor() {
        return counselor;
    }

    public void setCounselor(User counselor) {
        this.counselor = counselor;
    }

    @JsonSerialize(using = PublicSerializer.class)
    @JsonView(View.Peer.class)
    public User getDirector() {
        if (counselor == null) {
            return null;
        }
        if (counselor.isDirector()) {
            return counselor;
        }
        return counselor.getDirector();
    }

    public boolean isDirector() {
        return SecurityUtils.hasRole(this, AuthoritiesConstants.DIRECTOR);
    }

    public boolean isConselor() {
        return SecurityUtils.hasRole(this, AuthoritiesConstants.COUNSELOR);
    }

    @JsonSerialize(using = PublicSerializer.class)
    @JsonView(View.Peer.class)
    public User getGeneralManager() {
        if (counselor == null) {
            return null;
        }
        if (counselor.isGeneralManager()) {
            return counselor;
        }
        return counselor.getGeneralManager();
    }

    public boolean isGeneralManager() {
        return SecurityUtils.hasRole(this, AuthoritiesConstants.GENERAL_MANAGER);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTargetTitle() {
        return targetTitle;
    }

    public void setTargetTitle(String targetTitle) {
        this.targetTitle = targetTitle;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }
    
    @JsonView(View.Private.class)
    public Set<String> getRoles() {
       return authorities.stream().map(Authority::getName).collect(Collectors.toSet());
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public Set<Review> getSelfReviews() {
        return selfReviews;
    }

    public void setSelfReviews(Set<Review> selfReviews) {
        this.selfReviews = selfReviews;
    }

    public Set<Review> getColleagueReviews() {
        return colleagueReviews;
    }

    public void setColleagueReviews(Set<Review> colleagueReviews) {
        this.colleagueReviews = colleagueReviews;
    }

    public Set<Review> getPeerReviews() {
        return peerReviews;
    }

    public void setPeerReviews(Set<Review> reviews) {
        this.peerReviews = reviews;
    }

    public Set<PersistentToken> getPersistentTokens() {
        return persistentTokens;
    }

    public void setPersistentTokens(Set<PersistentToken> persistentTokens) {
        this.persistentTokens = persistentTokens;
    }
    
    public Set<SkillRanking> getSkillRanking() {
        return skillRanking;
    }

    public void setSkillRanking(Set<SkillRanking> skillRanking) {
        this.skillRanking = skillRanking;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        if (!login.equals(user.login)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
      if (login != null) {
        return login.hashCode();
      } else {
        return id.hashCode();
      }
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", employeeId='" + employeeId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                "}";
    }
    
    public String getFullName(){
    	String f = getFirstName();
    	String l = getLastName();
    	String fullName = "";
		if(f==null && l==null)
			fullName = "";
		else if(f!=null && l==null)
			fullName = f;
		else if(f==null && l!=null)
			fullName = l;
		else if(f!=null && l!=null)
			fullName = f + " " + l;
		
		return fullName;
    }
  
}
