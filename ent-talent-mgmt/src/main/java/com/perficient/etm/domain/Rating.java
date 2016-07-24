package com.perficient.etm.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.perficient.etm.domain.util.IdentitySerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Rating.
 */
@Entity
@Table(name = "T_RATING")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Rating implements Serializable {

    private static final long serialVersionUID = -6448720871314292221L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "score")
    private Double score;

    @Column(name = "comment")
    private String comment;

    @Column(name = "visible")
    private Boolean visible = true;

    @JsonSerialize(using = IdentitySerializer.class)
    @ManyToOne
    private Question question;

    @ManyToOne
    @JsonIgnore
    private Feedback feedback;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean isVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Rating rating = (Rating) o;

        if ( ! Objects.equals(id, rating.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        if(id == null && question != null) {
            return Objects.hashCode(question.getId());
        }
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Rating{" +
                "id=" + id +
                ", question='" + (question != null ? question.getId() : null ) + "'" +
                ", score='" + score + "'" +
                '}';
    }
}
