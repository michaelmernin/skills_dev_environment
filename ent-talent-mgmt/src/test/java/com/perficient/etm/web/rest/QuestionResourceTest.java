package com.perficient.etm.web.rest;

import com.perficient.etm.domain.Question;
import com.perficient.etm.repository.QuestionRepository;
import com.perficient.etm.utils.ResourceTestUtils;
import com.perficient.etm.utils.SpringAppTest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the QuestionResource REST controller.
 *
 * @see QuestionResource
 */
public class QuestionResourceTest extends SpringAppTest {

    private static final String DEFAULT_TEXT = "SAMPLE_TEXT";
    private static final String UPDATED_TEXT = "UPDATED_TEXT";

    private static final Integer DEFAULT_POSITION = 0;
    private static final Integer UPDATED_POSITION = 1;

    @Inject
    private QuestionRepository questionRepository;

    private MockMvc restQuestionMockMvc;

    private Question question;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        QuestionResource questionResource = new QuestionResource();
        ReflectionTestUtils.setField(questionResource, "questionRepository", questionRepository);
        this.restQuestionMockMvc = MockMvcBuilders.standaloneSetup(questionResource).build();
    }

    @Before
    public void initTest() {
        question = new Question();
        question.setText(DEFAULT_TEXT);
        question.setPosition(DEFAULT_POSITION);
    }

    @Test
    @Transactional
    public void createQuestion() throws Exception {
        int count = (int) questionRepository.count();

        // Create the Question
        restQuestionMockMvc.perform(post("/api/questions")
                .contentType(ResourceTestUtils.APPLICATION_JSON_UTF8)
                .content(ResourceTestUtils.convertObjectToJsonBytes(question)))
                .andExpect(status().isOk());

        // Validate the Question in the database
        List<Question> questions = questionRepository.findAll();
        assertThat(questions).hasSize(count + 1);
        Optional<Question> optional = questions.stream().filter(q -> {return DEFAULT_TEXT.equals(q.getText());}).findAny();
        assertThat(optional.isPresent()).isTrue();
        Question testQuestion = optional.get();
        assertThat(testQuestion.getText()).isEqualTo(DEFAULT_TEXT);
        assertThat(testQuestion.getPosition()).isEqualTo(DEFAULT_POSITION);
    }

    @Test
    @Transactional
    public void getAllQuestions() throws Exception {
        int count = (int) questionRepository.count();
        Question question = questionRepository.findOne(1L);

        // Get all the questions
        ResultActions result = restQuestionMockMvc.perform(get("/api/questions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(question.getId().intValue()))
                .andExpect(jsonPath("$[0].text").value(question.getText()))
                .andExpect(jsonPath("$[0].position").value(question.getPosition()));

        ResourceTestUtils.assertJsonCount(result, count);
    }

    @Test
    @Transactional
    public void getQuestion() throws Exception {
        // Initialize the database
        questionRepository.saveAndFlush(question);

        // Get the question
        restQuestionMockMvc.perform(get("/api/questions/{id}", question.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(question.getId().intValue()))
            .andExpect(jsonPath("$.text").value(DEFAULT_TEXT.toString()))
            .andExpect(jsonPath("$.position").value(DEFAULT_POSITION));
    }

    @Test
    @Transactional
    public void getNonExistingQuestion() throws Exception {
        // Get the question
        restQuestionMockMvc.perform(get("/api/questions/{id}", 404L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateQuestion() throws Exception {
        int count = (int) questionRepository.count();

        Question question = questionRepository.findOne(1L);

        // Update the question
        question.setText(UPDATED_TEXT);
        question.setPosition(UPDATED_POSITION);
        restQuestionMockMvc.perform(post("/api/questions")
                .contentType(ResourceTestUtils.APPLICATION_JSON_UTF8)
                .content(ResourceTestUtils.convertObjectToJsonBytes(question)))
                .andExpect(status().isOk());

        // Validate the Question in the database
        List<Question> questions = questionRepository.findAll();
        assertThat(questions).hasSize(count);
        Optional<Question> optional = questions.stream().filter(q -> {return q.getId() == 1L;}).findAny();
        assertThat(optional.isPresent()).isTrue();
        Question testQuestion = optional.get();
        assertThat(testQuestion.getText()).isEqualTo(UPDATED_TEXT);
        assertThat(testQuestion.getPosition()).isEqualTo(UPDATED_POSITION);
    }
}
