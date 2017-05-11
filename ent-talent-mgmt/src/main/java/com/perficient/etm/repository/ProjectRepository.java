package com.perficient.etm.repository;

import com.perficient.etm.domain.Project;
import com.perficient.etm.domain.User;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Project entity.
 */
public interface ProjectRepository extends JpaRepository<Project,Long> {

    @Query("select project from Project project where project.manager.login = ?#{principal.username}")
    List<Project> findByManagedprojectsIsCurrentUser();

    List<Project> findAllByManager(User user);
}
