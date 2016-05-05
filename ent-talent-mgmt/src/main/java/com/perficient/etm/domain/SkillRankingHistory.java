
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
import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Skill Ranking History.
 */
@Entity
@Table(name = "T_SKILLRANKINGHISTORY")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SkillRankingHistory implements Serializable{

    private static final long serialVersionUID = -8626613765401143055L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;

    @Column(name = "rank")
    private Integer rank;

    @ManyToOne
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

    public SkillRankingHistory(){

    }

    public SkillRankingHistory(SkillRanking ranking){
        setUser(ranking.getUser());
        setSkill(ranking.getSkill());
        setDateTime(ranking.getDateTime());
        setRank(ranking.getRank());
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

    @JsonIgnore
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

        SkillRankingHistory skillRankingHistory = (SkillRankingHistory) o;

        if (id != null ? !id.equals(skillRankingHistory.id) : skillRankingHistory.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int i = 1;
        i = i*7+(id==null ? 0 :id.intValue());
        i = i*13+(rank==null ? 0 :rank);
        i = i*19+(datetime==null ? 0 :datetime.hashCode());
        i = i*23+(user==null ? 0 :user.hashCode());
        return i;
    }

    @Override
    public String toString() {
        return "SkillRankingHistory{" +
                "id=" + id +
                ", Rank='" + rank + "'" +
                ", Date='" + datetime + "'" +
                ", User='" + user + "'" +
                '}';
    }
}
