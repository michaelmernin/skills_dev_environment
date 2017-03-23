package com.perficient.etm.web.rest;

import com.perficient.etm.utils.SpringAppTest;

/**
 * Test class for the ProjectResource REST controller.
 *
 * @see ProjectResource
 */
// @RunWith(SpringRunner.class)
// @SpringBootTest(classes = EtmApp.class)
public class ProjectResourceIntTest extends SpringAppTest {
    //
    // private static final String DEFAULT_NAME = "AAAAAAAAAA";
    // private static final String UPDATED_NAME = "BBBBBBBBBB";
    //
    // private static final String DEFAULT_MANAGER = "AAAAAAAAAA";
    // private static final String UPDATED_MANAGER = "BBBBBBBBBB";
    //
    // private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    // private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";
    //
    // private static final String DEFAULT_CLIENT = "AAAAAAAAAA";
    // private static final String UPDATED_CLIENT = "BBBBBBBBBB";
    //
    // @Autowired
    // private ProjectRepository projectRepository;
    //
    // @Autowired
    // private MappingJackson2HttpMessageConverter jacksonMessageConverter;
    //
    // @Autowired
    // private PageableHandlerMethodArgumentResolver pageableArgumentResolver;
    //
    // @Autowired
    // private EntityManager em;
    //
    // private MockMvc restProjectMockMvc;
    //
    // private Project project;
    //
    // @Before
    // public void setup() {
    // MockitoAnnotations.initMocks(this);
    // ProjectResource projectResource = new ProjectResource();
    // this.restProjectMockMvc =
    // MockMvcBuilders.standaloneSetup(projectResource)
    // .setCustomArgumentResolvers(pageableArgumentResolver).setMessageConverters(jacksonMessageConverter)
    // .build();
    // }
    //
    // /**
    // * Create an entity for this test.
    // *
    // * This is a static method, as tests for other entities might also need
    // it,
    // * if they test an entity which requires the current entity.
    // */
    // public static Project createEntity(EntityManager em) {
    // Project project = new
    // Project().name(DEFAULT_NAME).manager(DEFAULT_MANAGER).description(DEFAULT_DESCRIPTION)
    // .client(DEFAULT_CLIENT);
    // return project;
    // }
    //
    // @Before
    // public void initTest() {
    // project = createEntity(em);
    // }
    //
    // @Test
    // @Transactional
    // public void createProject() throws Exception {
    // int databaseSizeBeforeCreate = projectRepository.findAll().size();
    //
    // // Create the Project
    //
    // restProjectMockMvc.perform(post("/api/projects")).andExpect(status().isOk()));
    //
    // }
    //
    // @Test
    // @Transactional
    // public void createProjectWithExistingId() throws Exception {
    // int databaseSizeBeforeCreate = projectRepository.findAll().size();
    //
    // // Create the Project with an existing ID
    // Project existingProject = new Project();
    // existingProject.setId(1L);
    //
    // // An entity with an existing ID cannot be created, so this API call
    // // must fail
    // restProjectMockMvc.perform(post("/api/projects").contentType(MediaType.APPLICATION_JSON_UTF8))
    // .andExpect(status().isBadRequest());
    //
    // // Validate the Alice in the database
    // List<Project> projectList = projectRepository.findAll();
    // assertThat(projectList).hasSize(databaseSizeBeforeCreate);
    // }
    //
    // @Test
    // @Transactional
    // public void checkNameIsRequired() throws Exception {
    // int databaseSizeBeforeTest = projectRepository.findAll().size();
    // // set the field null
    // project.setName(null);
    //
    // // Create the Project, which fails.
    //
    // restProjectMockMvc.perform(post("/api/projects")).andExpect(status().isBadRequest());
    //
    // List<Project> projectList = projectRepository.findAll();
    // assertThat(projectList).hasSize(databaseSizeBeforeTest);
    // }
    //
    // @Test
    // @Transactional
    // public void checkManagerIsRequired() throws Exception {
    // int databaseSizeBeforeTest = projectRepository.findAll().size();
    // // set the field null
    // project.setManager(null);
    //
    // // Create the Project, which fails.
    //
    // restProjectMockMvc.perform(post("/api/projects")).andExpect(status().isBadRequest());
    //
    // List<Project> projectList = projectRepository.findAll();
    // assertThat(projectList).hasSize(databaseSizeBeforeTest);
    // }
    //
    // @Test
    // @Transactional
    // public void getAllProjects() throws Exception {
    // // Initialize the database
    // int count = (int) projectRepository.count();
    // projectRepository.saveAndFlush(project);
    //
    // // Get all the projectList
    // ResultActions result =
    // restProjectMockMvc.perform(get("/api/projects?sort=id,desc")).andExpect(status().isOk())
    // .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
    // .andExpect(jsonPath("$.[*].id").value(project.getId().intValue()))
    // .andExpect(jsonPath("$.[*].name").value(DEFAULT_NAME.toString()))
    // .andExpect(jsonPath("$.[*].manager").value(DEFAULT_MANAGER.toString()))
    // .andExpect(jsonPath("$.[*].description").value(DEFAULT_DESCRIPTION.toString()))
    // .andExpect(jsonPath("$.[*].client").value(DEFAULT_CLIENT.toString()));
    //
    // ResourceTestUtils.assertJsonCount(result, count);
    // }
    //
    // @Test
    // @Transactional
    // public void getProject() throws Exception {
    // // Initialize the database
    // projectRepository.saveAndFlush(project);
    //
    // // Get the project
    // restProjectMockMvc.perform(get("/api/projects/{id}",
    // project.getId())).andExpect(status().isOk())
    // .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
    // .andExpect(jsonPath("$.id").value(project.getId().intValue()))
    // .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
    // .andExpect(jsonPath("$.manager").value(DEFAULT_MANAGER.toString()))
    // .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
    // .andExpect(jsonPath("$.client").value(DEFAULT_CLIENT.toString()));
    // }
    //
    // @Test
    // @Transactional
    // public void getNonExistingProject() throws Exception {
    // // Get the project
    // restProjectMockMvc.perform(get("/api/projects/{id}",
    // Long.MAX_VALUE)).andExpect(status().isNotFound());
    // }
    //
    // @Test
    // @Transactional
    // public void updateProject() throws Exception {
    // // Initialize the database
    // projectRepository.saveAndFlush(project);
    // int databaseSizeBeforeUpdate = projectRepository.findAll().size();
    //
    // // Update the project
    // Project updatedProject = projectRepository.findOne(project.getId());
    // updatedProject.name(UPDATED_NAME).manager(UPDATED_MANAGER).description(UPDATED_DESCRIPTION)
    // .client(UPDATED_CLIENT);
    //
    // restProjectMockMvc.perform(put("/api/projects")).andExpect(status().isOk());
    //
    // }
    //
    // @Test
    // @Transactional
    // public void updateNonExistingProject() throws Exception {
    // int databaseSizeBeforeUpdate = projectRepository.findAll().size();
    //
    // // Create the Project
    //
    // // If the entity doesn't have an ID, it will be created instead of just
    // // being updated
    // restProjectMockMvc.perform(put("/api/projects")).andExpect(status().isCreated());
    //
    // // Validate the Project in the database
    // List<Project> projectList = projectRepository.findAll();
    // assertThat(projectList).hasSize(databaseSizeBeforeUpdate + 1);
    // }
    //
    // @Test
    // @Transactional
    // public void deleteProject() throws Exception {
    // // Initialize the database
    // projectRepository.saveAndFlush(project);
    // int databaseSizeBeforeDelete = projectRepository.findAll().size();
    //
    // // Get the project
    // restProjectMockMvc.perform(delete("/api/projects/{id}",
    // project.getId()).accept(TestUtil.APPLICATION_JSON_UTF8))
    // .andExpect(status().isOk());
    //
    // // Validate the database is empty
    // List<Project> projectList = projectRepository.findAll();
    // assertThat(projectList).hasSize(databaseSizeBeforeDelete - 1);
    // }

}
