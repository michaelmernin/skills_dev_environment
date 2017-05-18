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

    @Query("select p from Project p where p.manager = ?1 or ?1 member of p.projectMembers")
    List<Project> findAllByUser(User user);
}
