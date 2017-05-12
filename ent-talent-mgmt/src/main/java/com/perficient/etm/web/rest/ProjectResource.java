package com.perficient.etm.web.rest;

import java.net.URISyntaxException;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.perficient.etm.domain.Project;
import com.perficient.etm.domain.User;
import com.perficient.etm.repository.ProjectRepository;
import com.perficient.etm.security.AuthoritiesConstants;
import com.perficient.etm.service.UserService;

/**
 * REST controller for managing Project.
 */
@RestController
@RequestMapping("/api")
public class ProjectResource {

    private final Logger log = LoggerFactory.getLogger(ProjectResource.class);

    private static final String ENTITY_NAME = "project";

    @Inject
    private ProjectRepository projectRepository;
    
    @Inject
    private UserService userService;

    /**
     * POST /projects : Create a new project.
     *
     * @param project
     *            the project to create
     * @return the ResponseEntity with status 201 (Created) and with body the
     *         new project, or with status 400 (Bad Request) if the project has
     *         already an ID
     * @throws URISyntaxException
     *             if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/projects", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Project> createProject(@RequestBody Project project) throws URISyntaxException {

        if(project.getId() != null && projectRepository.exists(project.getId())) return ResponseEntity.badRequest().body(null);
        Project createdProj = projectRepository.save(project);
        return ResponseEntity.ok().body(createdProj);
    }

    /**
     * PUT /projects : Updates an existing project.
     *
     * @param project
     *            the project to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated
     *         project, or with status 400 (Bad Request) if the project is not
     *         valid, or with status 500 (Internal Server Error) if the project
     *         couldnt be updated
     * @throws URISyntaxException
     *             if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/projects", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Project> updateProject(@Valid @RequestBody Project project) throws URISyntaxException {
        log.debug("REST request to update Project : {}", project);
        if (project.getId() == null) {
            return createProject(project);
        }
        Project updatedProj = projectRepository.save(project);
        return ResponseEntity.ok().body(updatedProj);
    }

    /**
     * GET /projects : get all the projects.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of projects
     *         in body
     */
    @RequestMapping(value = "/projects", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public List<Project> getAllProjects() {
        log.debug("REST request to get all Projects");
        List<Project> projects = projectRepository.findAll();
        return projects;
    }

    /**
     * GET /project/:id : get the "id" project.
     *
     * @param id
     *            the id of the project to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the
     *         project, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/projects/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Project> getProject(@PathVariable Long id) {
        log.debug("REST request to get Project : {}", id);
        Project project = projectRepository.findOne(id);
        return ResponseEntity.ok(project);
    }

    /**
     * DELETE /projects/:id : delete the "id" project.
     *
     * @param id
     *            the id of the project to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/projects/{id}", method = RequestMethod.DELETE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        log.debug("REST request to delete Project : {}", id);
        projectRepository.delete(id);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /projects -> get all the projects by user id.
     */
    @RequestMapping(value = "/projects/byManager/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Project> getProjectsByManagerId(@PathVariable Long id) {
        log.debug("REST request to get projects by user");
        User user = userService.getUser(id);
        return projectRepository.findAllByManager(user);
    }
}
