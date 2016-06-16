
package com.perficient.etm.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

/**
 * A Skill.
 */
@Entity
@Table(name = "T_SKILL")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Skill implements Serializable {

    private static final long serialVersionUID = -3560675208797510412L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name="skillcategory_id", insertable = false, updatable = false)
    private Long skillCategoryId;
    
    @Column(name = "name")
    private String name;

    @ManyToOne
    @JsonBackReference
    private SkillCategory skillcategory;
    
    @OneToMany(mappedBy = "skill", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<SkillRanking> rankings;

    @Column(name = "enabled_flag")
    @Type(type="org.hibernate.type.NumericBooleanType")
    private Boolean enabled;

    public Skill(){
     
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

   // @JsonIgnore
    public SkillCategory getSkillcategory() {
        return skillcategory;
    }

    public void setSkillcategory(SkillCategory skillcategory) {
        this.skillcategory = skillcategory;
    }

    public List<SkillRanking> getRankings() {
        return rankings;
    }

    public void setRankings(List<SkillRanking> rankings) {
        this.rankings = rankings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Skill skill = (Skill) o;

        if (id != null ? !id.equals(skill.id) : skill.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int i = 1;
        i = i*11+(id==null ? 0 :id.intValue());
        i = i*17+(name==null ? 0 :name.hashCode());
        return i;
    }

    @Override
    public String toString() {
        return "Skill{" +
                "id=" + id +
                ", Name='" + name + "'" +
                '}';
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getSkillCategoryId() {
        return skillCategoryId;
    }

    public void setSkillCategoryId(Long skillCategoryId) {
        this.skillCategoryId = skillCategoryId;
    }
}
