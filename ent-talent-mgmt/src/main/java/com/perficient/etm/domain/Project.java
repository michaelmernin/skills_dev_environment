package com.perficient.etm.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Project.
 */
@Entity
@Table(name = "project")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "manager", nullable = false)
    private String manager;

    @Column(name = "description")
    private String description;

    @Column(name = "client")
    private String client;

    @ManyToOne
    private User managedprojects;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Project name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManager() {
        return manager;
    }

    public Project manager(String manager) {
        this.manager = manager;
        return this;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getDescription() {
        return description;
    }

    public Project description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClient() {
        return client;
    }

    public Project client(String client) {
        this.client = client;
        return this;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public User getManagedprojects() {
        return managedprojects;
    }

    public Project managedprojects(User user) {
        this.managedprojects = user;
        return this;
    }

    public void setManagedprojects(User user) {
        this.managedprojects = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Project project = (Project) o;
        if (project.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Project{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", manager='" + manager + "'" +
            ", description='" + description + "'" +
            ", client='" + client + "'" +
            '}';
    }
}
