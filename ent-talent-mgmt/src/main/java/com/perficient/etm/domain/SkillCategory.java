
package com.perficient.etm.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * A SkillCategory.
 */
@Entity
@Table(name = "T_SKILLCATEGORY")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SkillCategory implements Serializable {

    private static final long serialVersionUID = -2088738917740248293L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "title")
    private String title;

    @OneToMany(mappedBy = "skillcategory",fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Skill> skills;
    
    @Column(name = "enabled_flag")
    @Type(type="org.hibernate.type.NumericBooleanType")
    private Boolean enabled;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SkillCategory skillCategory = (SkillCategory) o;

        if (id != null ? !id.equals(skillCategory.id) : skillCategory.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int i = 1;
        i = i*11+(id==null ? 0 :id.intValue());
        i = i*17+(title==null ? 0 :title.hashCode());
        return i;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
