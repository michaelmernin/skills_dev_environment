
package com.perficient.etm.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.perficient.etm.domain.util.CustomLocalDateSerializer;
import com.perficient.etm.domain.util.ISO8601LocalDateDeserializer;

/**
 * Skill Ranking.
 */
@Entity
@Table(name = "T_SKILLRANKING")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SkillRanking implements Serializable {

    private static final long serialVersionUID = 2795196200930205346L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @Column(name = "rank")
    private Integer rank;
    
    @ManyToOne
    @JsonBackReference
    private Skill skill;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
   /* @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)*/
    @JsonIgnore
    @Column(name = "date")
    private LocalDateTime datetime;

    @ManyToOne
    @JsonIgnore
    private User user;

    public SkillRanking(){
     
    }
    
    public SkillRanking(User user, Skill skill){
        this();
        this.user = user;
        this.skill = skill;
        this.rank = 0;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getDateTime() {
        return datetime;
    }

    public void setDateTime(LocalDateTime date) {
        this.datetime = date;
    }

    public Skill getSkill() {
        return skill;
    }
    
    @JsonProperty
    public void setSkill(Skill skill) {
        this.skill = skill;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SkillRanking skillRanking = (SkillRanking) o;

        if (id != null ? !id.equals(skillRanking.id) : skillRanking.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int i = 1;
        i = i*11+(id==null ? 0 :id.intValue());
        i = i*17+(rank==null ? 0 :rank);
        i = i*13+(datetime==null ? 0 :datetime.hashCode());
        i = i*31+(user==null ? 0 :user.hashCode());
        return i;
    }

    @Override
    public String toString() {
        return "SkillRanking{" +
                "id=" + id +
                ", Rank='" + rank + "'" +
                ", Date='" + datetime + "'" +
                ", User='" + user + "'" +
                '}';
    }
    
    public String toJsonDbWriteString() {
        return "{\n" +
                "\"id\" : " + id + ",\n" +
                "\"rank\" : " + rank + ",\n" +
                "\"date\" : \"" + datetime + "\",\n" +
                "\"user\" : {\n\"id\" : " + "3" + ",\n}\n" +
                "\"skill\" : {\n\"id\" : " + skill.getId() +
                "\n}\n" +
                '}';
    }
}
