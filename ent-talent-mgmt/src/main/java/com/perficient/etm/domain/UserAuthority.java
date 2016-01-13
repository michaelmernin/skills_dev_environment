package com.perficient.etm.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.io.Serializable;

/**
 * An authority (a security role) used by Spring Security.
 */
@Entity
@IdClass(UserAuthority.class)
@Table(name = "T_USER_AUTHORITY")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class UserAuthority implements Serializable {

    private static final long serialVersionUID = 2662495204798997264L;

    @NotNull
    @Id
    @Size(min = 0, max = 50)
    @Column(name = "authority_name", length = 50)
    private String authorityName;

    @NotNull
    @Id
    @Column(name = "user_id")
    private Long userId;

    public String getAuthorityName() {
        return authorityName;
    }

    public void setAuthorityName(String authorityName) {
        this.authorityName = authorityName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserAuthority UserAuthority = (UserAuthority) o;

        if (authorityName != null ? !authorityName.equals(UserAuthority.authorityName) : UserAuthority.authorityName != null) {
            return false;
        }

        if (userId != null ? !userId.equals(UserAuthority.userId) : UserAuthority.userId != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return authorityName.concat(userId.toString()).hashCode();
    }

    @Override
    public String toString() {
        return "Authority{" +
                "userId='" + userId + '\'' +
                ", authorityName='" + authorityName + '\'' +
                "}";
    }
}
