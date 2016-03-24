package com.perficient.etm.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;
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
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.perficient.etm.domain.util.CustomLocalDateSerializer;
import com.perficient.etm.domain.util.ISO8601LocalDateDeserializer;
import com.perficient.etm.domain.util.PeerSerializer;
import com.perficient.etm.domain.util.PublicSerializer;
import com.perficient.etm.domain.util.ReviewStatusConverter;
import com.perficient.etm.web.view.View;

/**
 * A Review.
 */
@Entity
@Table(name = "T_REVIEW")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Review implements Serializable {

    private static final long serialVersionUID = 2795196200930205346L;

    @JsonView(View.Identity.class)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonView(View.Public.class)
    @Column(name = "title")
    private String title;

    @JsonView(View.Public.class)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @JsonView(View.Public.class)
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @JsonView(View.Public.class)
    @Column(name = "client")
    private String client;

    @JsonView(View.Public.class)
    @Column(name = "project")
    private String project;

    @JsonView(View.Public.class)
    @Column(name = "role")
    private String role;

    @JsonView(View.Public.class)
    @Column(name = "responsibilities")
    private String responsibilities;

    @JsonView(View.Public.class)
    @Column(name = "rating")
    private Double rating;

    @JsonView(View.Public.class)
    @ManyToOne
    private ReviewType reviewType;

    @JsonView(View.Public.class)
    @Column(name = "reviewstatus_id")
    @Convert(converter = ReviewStatusConverter.class)
    private ReviewStatus reviewStatus;

    @JsonView(View.Public.class)
    @JsonSerialize(using = PeerSerializer.class)
    @ManyToOne
    private User reviewee;

    @JsonView(View.Public.class)
    @JsonSerialize(using = PublicSerializer.class)
    @ManyToOne
    private User reviewer;

    @JsonView(View.Public.class)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "T_PEER",
            joinColumns = {@JoinColumn(name = "review_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<User> peers = new HashSet<>();

    @OneToMany(mappedBy = "review")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Feedback> feedback;

    @JsonView(View.Public.class)
    @OneToMany(mappedBy = "review")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Goal> goals;

    @JsonView(View.Public.class)
    @Column(name = "process_id")
    private String processId;

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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public Set<Feedback> getFeedback() {
        return feedback;
    }

    public void setFeedback(Set<Feedback> feedback) {
        this.feedback = feedback;
    }

    public Set<Goal> getGoals() {
        return goals;
    }

    public void setGoals(Set<Goal> goals) {
        this.goals = goals;
    }

    public Set<User> getPeers() {
        return peers;
    }

    public void setPeers(Set<User> peers) {
        this.peers = peers;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(String responsibilities) {
        this.responsibilities = responsibilities;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public ReviewType getReviewType() {
        return reviewType;
    }

    public void setReviewType(ReviewType reviewType) {
        this.reviewType = reviewType;
    }

    public ReviewStatus getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(ReviewStatus reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public User getReviewee() {
        return reviewee;
    }

    public void setReviewee(User user) {
        this.reviewee = user;
    }

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User user) {
        this.reviewer = user;
    }

    public void sanitize() {
        id = null;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Review review = (Review) o;

        if (id != null ? !id.equals(review.id) : review.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", status='" + reviewStatus + "'" +
                ", reviewee='" + (reviewee != null ? reviewee.getLogin() : null) + "'" +
                ", reviewer='" + (reviewer != null ? reviewer.getLogin() : null) + "'" +
                '}';
    }
}
