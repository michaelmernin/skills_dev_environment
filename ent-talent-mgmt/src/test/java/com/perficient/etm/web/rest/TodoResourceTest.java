package com.perficient.etm.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.ReviewType;
import com.perficient.etm.domain.User;
import com.perficient.etm.exception.ETMException;
import com.perficient.etm.repository.ReviewTypeRepository;
import com.perficient.etm.service.ReviewService;
import com.perficient.etm.service.TodoService;
import com.perficient.etm.utils.ResourceTestUtils;
import com.perficient.etm.utils.SpringAppTest;

/**
 * Test class for the TodoResource REST controller.
 *
 * @see TodoResource
 */
public class TodoResourceTest extends SpringAppTest {

    private static final String DEFAULT_TITLE = "SAMPLE_TEXT";

    private static final LocalDate DEFAULT_START_DATE = new LocalDate(0L);

    private static final LocalDate DEFAULT_END_DATE = DEFAULT_START_DATE.plusYears(1);
    
    private static final String DEFAULT_CLIENT = "SAMPLE_TEXT";
    
    private static final String DEFAULT_ROLE = "SAMPLE_TEXT";
    
    private static final String DEFAULT_RESPONSIBILITIES = "SAMPLE_TEXT";

    private static final Double DEFAULT_RATING = 0.0;

    @Inject
    private ReviewService reviewService;

    @Inject
    private TodoService todoService;

    @Inject
    private ReviewTypeRepository reviewTypeRepository;
    
    private MockMvc restTodoMockMvc;

    private Review review;

    @PostConstruct
    public void setup() throws ETMException {
        MockitoAnnotations.initMocks(this);
        TodoResource todoResource = new TodoResource();
        ReflectionTestUtils.setField(todoResource, "todoService", todoService);

        this.restTodoMockMvc = ResourceTestUtils.exceptionHandlingMockMvc(todoResource).build();
    }

    @Before
    public void initTest() {
        review = new Review();
        review.setTitle(DEFAULT_TITLE);
        review.setStartDate(DEFAULT_START_DATE);
        review.setEndDate(DEFAULT_END_DATE);
        review.setClient(DEFAULT_CLIENT);
        review.setRole(DEFAULT_ROLE);
        review.setResponsibilities(DEFAULT_RESPONSIBILITIES);
        review.setRating(DEFAULT_RATING);
        User reviewee = new User();
        reviewee.setId(7L);
        review.setReviewee(reviewee);
        ReviewType revType = reviewTypeRepository.findOne(1L);
        review.setReviewType(revType);
    }

    @Test
    @WithUserDetails("dev.user7")
    public void getTodoList() throws Exception {
        // Start a review process to generate tasks
        reviewService.startReviewProcess(review);

        restTodoMockMvc.perform(get("/api/todos").accept(ResourceTestUtils.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.").isArray())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].userId").value(7))
                .andExpect(jsonPath("$[0].review").exists());
    }
    
    @Test
    @WithUserDetails("dev.user7")
    public void getActiveTodo() throws Exception {
        // Start a review process to generate tasks
        reviewService.startReviewProcess(review);

        restTodoMockMvc.perform(get("/api/reviews/{id}/todo", review.getId()).accept(ResourceTestUtils.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                // .andDo(print())
                .andExpect(jsonPath("$.").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.userId").value(7))
                .andExpect(jsonPath("$.review").exists());
    }
}
