package com.perficient.etm.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Set;

/**
 * A ReviewType.
 */
@Entity
@Table(name = "T_REVIEWTYPE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ReviewType implements Serializable {

    private static final long serialVersionUID = -110339610750395967L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "active")
    private Boolean active;
    
    @Column(name = "interval")
    @Enumerated(EnumType.STRING)
    private ReviewInterval interval;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;
    
    @Column(name = "version")
    private Integer version;
    
    @OneToMany(mappedBy = "reviewType", fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Question> questions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public ReviewInterval getInterval() {
        return interval;
    }
    
    public void setInterval(ReviewInterval interval) {
        this.interval = interval;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ReviewType reviewType = (ReviewType) o;

        if (id != null ? !id.equals(reviewType.id) : reviewType.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "ReviewType{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", version='" + version + "'" +
                ", active='" + active + "'" +
                ", description='" + description + "'" +
                '}';
    }
}
