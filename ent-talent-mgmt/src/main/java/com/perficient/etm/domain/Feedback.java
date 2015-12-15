package com.perficient.etm.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.perficient.etm.domain.util.FeedbackStatusConverter;
import com.perficient.etm.domain.util.PublicSerializer;
/**
 * A Feedback.
 */
@Entity
@Table(name = "T_FEEDBACK")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Feedback implements Serializable {

    private static final long serialVersionUID = 4925837454082432014L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "overall_score")
    private Double overallScore;

    @Column(name = "overall_comment")
    private String overallComment;
    
    @ManyToOne
    private Review review;

    @JsonSerialize(using = PublicSerializer.class)
    @ManyToOne
    private User author;

    @ManyToOne
    private FeedbackType feedbackType;

    @Column(name = "feedbackstatus_id")
    @Convert(converter = FeedbackStatusConverter.class)
    private FeedbackStatus feedbackStatus;

    @OneToMany(mappedBy = "feedback", fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Rating> ratings = new HashSet<>();

    //@Transient
    @Column(name="activiti_process_id")
    private String feedbackProcessId;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getOverallScore() {
		return overallScore;
	}

	public void setOverallScore(Double overallScore) {
		this.overallScore = overallScore;
	}

	public String getOverallComment() {
		return overallComment;
	}

	public void setOverallComment(String overallComment) {
		this.overallComment = overallComment;
	}

	@JsonIgnore
    public Review getReview() {
        return review;
    }

    @JsonProperty
    public void setReview(Review review) {
        this.review = review;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User user) {
        this.author = user;
    }

    public FeedbackType getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(FeedbackType feedbackType) {
        this.feedbackType = feedbackType;
    }

    public FeedbackStatus getFeedbackStatus() {
        return feedbackStatus;
    }

    public void setFeedbackStatus(FeedbackStatus feedbackStatus) {
        this.feedbackStatus = feedbackStatus;
    }

    public Set<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(Set<Rating> ratings) {
        this.ratings = ratings;
    }
    
    public String getFeedbackProcessId() {
		return feedbackProcessId;
	}

	public void setFeedbackProcessId(String feedbackProcessId) {
		this.feedbackProcessId = feedbackProcessId;
	}

	public boolean belongsToAuthor(User peer){
		if (peer == null || this.author == null)
			return false;
		return (this.author.getId().equals(peer.getId()));
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Feedback feedback = (Feedback) o;

        if ( ! Objects.equals(id, feedback.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", review='" + (review != null ? review.getId() : null) + "'" +
                ", author='" + (author != null ? author.getId() : null) + "'" +
                ", type='" + (feedbackType != null ? feedbackType.getId() : null) + "'" +
                ", status='" + (feedbackStatus != null ? feedbackStatus.getId() : null) + "'" +
                '}';
    }
}
