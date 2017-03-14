package com.perficient.etm.repository;

import com.perficient.etm.domain.Project;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Project entity.
 */
@SuppressWarnings("unused")
public interface ProjectRepository extends JpaRepository<Project,Long> {

    @Query("select project from Project project where project.managedprojects.login = ?#{principal.username}")
    List<Project> findByManagedprojectsIsCurrentUser();

}
