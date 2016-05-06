package com.perficient.etm.workflow;

import static java.lang.System.out;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.ProcessInstanceHistoryLog;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.perficient.etm.domain.Review;
import com.perficient.etm.domain.ReviewStatus;
import com.perficient.etm.domain.TodoResult;
import com.perficient.etm.repository.ReviewRepository;
import com.perficient.etm.service.activiti.ProcessConstants;
import com.perficient.etm.utils.SpringAppTest;

public class AnnualReviewTest extends SpringAppTest {

    private static final long REVIEW_ID = 1L;

    @Autowired
    private RuntimeService runtimeSvc;

    @Autowired
    private TaskService taskSvc;

    @Autowired
    private HistoryService historySvc;

    @Inject
    private ReviewRepository reviewRepository;

    @Test
    public void shouldCompleteReview() {
        ProcessInstance processInstance = runtimeSvc.startProcessInstanceByKey("annualReviewTest");
        assertNotNull(processInstance);
        System.out.println(processInstance.getActivityId());
    }

    private Map<String, Object> getProcessVariables() {
        Map<String,Object> variables = new HashMap<>();
        variables.put(ProcessConstants.REVIEW_VARIABLE, REVIEW_ID);
        variables.put(ProcessConstants.REVIEWEE_VARIABLE, "Alex");
        variables.put(ProcessConstants.REVIEWER_VARIABLE, "Craig");
        variables.put(ProcessConstants.DIRECTOR_VARIABLE, "David");
        variables.put(ProcessConstants.GENERAL_MANAGER_VARIABLE, "Art");
        variables.put(ProcessConstants.REVIEWEE_FEEDBACK_VARIABLE, 1L);
        variables.put(ProcessConstants.REVIEWER_FEEDBACK_VARIABLE, 2L);
        return variables;
    }

    @Test
    public void testAnnualReview() {

        Map<String,Object> variables = getProcessVariables();
        ProcessInstance processInstance = runtimeSvc.startProcessInstanceByKey("annualReview",variables);
        assertNotNull(processInstance);
        System.out.println(processInstance.getActivityId());

        List<Task> list = taskSvc.createTaskQuery().taskAssignee("Alex").list();
        List<Task> list2 = taskSvc.createTaskQuery().taskAssignee("Craig").list();

        out.println(list);

        assert(list.size() == 1);
        assert(list2.size() == 0);

        //Complete the first one and check for the task of the reviewer
        taskSvc.complete(list.get(0).getId());

        list = taskSvc.createTaskQuery().taskAssignee("Alex").list();
        list2 = taskSvc.createTaskQuery().taskAssignee("Craig").list();

        assert(list.size() == 0);
        assert(list2.size() == 1);

    }

    @Test
    public void testCancel() {
        Map<String,Object> variables = getProcessVariables();
        ProcessInstance processInstance = runtimeSvc.startProcessInstanceByKey("annualReview",variables);
        assertNotNull(processInstance);

        runtimeSvc.deleteProcessInstance(processInstance.getProcessInstanceId(), "Cancel Process");
    }

    @Test(expected = ActivitiObjectNotFoundException.class)
    public void testdeleteProcessNotFound() {
        runtimeSvc.deleteProcessInstance("engagementReview:123", "Test Delete");
    }

    @Test
    public void testHappyPath() {
        Map<String,Object> variables = getProcessVariables();
        ProcessInstance processInstance = runtimeSvc.startProcessInstanceByKey("annualReview",variables);

        //Self Feedback
        Task t = getNextTask(processInstance.getId());
        assertNotNullAndAsignee(t,"Alex");
        Review review = reviewRepository.getOne(REVIEW_ID);
        assertEquals("Review should start Open", ReviewStatus.OPEN, review.getReviewStatus());

        //Reviewer Feedback
        t = completeAndGetNextTask(t,processInstance.getId());
        assertNotNullAndAsignee(t,"Craig");
        
        //Reviewer Curation
        t = completeAndGetNextTask(t,processInstance.getId());
        assertNotNullAndAsignee(t,"Craig");

        //Directors Approval
        t = completeAndGetNextTask(t,processInstance.getId());
        assertNotNullAndAsignee(t,"David");
        review = reviewRepository.getOne(REVIEW_ID);
        assertEquals("Review should move to Director Approval", ReviewStatus.DIRECTOR_APPROVAL, review.getReviewStatus());

        //Joint Approvals
        List<Task> tasks = completeAndGetParallelTasks(t,processInstance.getId());
        assertEquals("Expect two parallel joint review tasks", 2, tasks.size());
        assertNotNullAndAsignee(tasks.get(0), "Craig");
        assertNotNullAndAsignee(tasks.get(1), "Alex");
        review = reviewRepository.getOne(REVIEW_ID);
        assertEquals("Review should move to Joint Approval", ReviewStatus.JOINT_APPROVAL, review.getReviewStatus());

        //General Manager Approval
        t = completeAndGetNextTask(tasks, processInstance.getId());
        assertNotNullAndAsignee(t,"Art");
        review = reviewRepository.getOne(REVIEW_ID);
        assertEquals("Review should move to GM Approval", ReviewStatus.GM_APPROVAL, review.getReviewStatus());

        //Process should be done by now
        t = completeAndGetNextTask(t,processInstance.getId());
        assertNull(t);
        review = reviewRepository.getOne(REVIEW_ID);
        assertEquals("Review should move to Complete", ReviewStatus.COMPLETE, review.getReviewStatus());

        //Save the process Instance Id
        String processInstanceId = processInstance.getId();
        //check this if historical data is enabled and check for isEnded() method. might be different
        processInstance = runtimeSvc.createProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult();
        assertNull(processInstance);

        ProcessInstanceHistoryLog result = historySvc.createProcessInstanceHistoryLogQuery(processInstanceId).singleResult();
        assertNotNull(result);
        assertNotNull("There should be an end time for the finished process",result.getEndTime());
    }

    @Test
    public void testReviewersReject() {
        Map<String,Object> variables = getProcessVariables();
        ProcessInstance processInstance = runtimeSvc.startProcessInstanceByKey("annualReview",variables);

        //Self Feedback
        Task t = getNextTask(processInstance.getId());
        assertNotNullAndAsignee(t,"Alex");

        //Reviewer Feedback
        t = completeAndGetNextTask(t, processInstance.getId());
        assertNotNullAndAsignee(t,"Craig");
        
        //Reviewer Curation
        t = completeAndGetNextTask(t, processInstance.getId());
        assertNotNullAndAsignee(t,"Craig");
        
        //Fail the reviewer curation
        t = completeWithAndGetNextTask(t, TodoResult.REJECT, processInstance.getId());

        //flow has to go back to initial reviewer
        assertNotNullAndAsignee(t,"Alex");
    }

    /**
     * Completes the current task with RESULT variable as TRUE
     * by calling the TaskService.complete method
     * @param t The Task object to complete
     */
    @SuppressWarnings("unused")
    private void completeATaskWithSucess(Task t) {
        completeATaskWith(t, TodoResult.SUBMIT);
    }

    /**
     * Completes the current task with RESULT variable as specified
     * in the parameters and by calling the TaskService.complete method
     * @param t The Task object to complete
     * @param result The String result to set in the complete variables
     */
    private void completeATaskWith(Task t, TodoResult result) {
        completeATaskWith(t, result, ProcessConstants.RESULT_VARIABLE);
    }
    
    /**
     * Completes the current task with RESULT variable as specified
     * in the parameters and by calling the TaskService.complete method
     * @param t The Task object to complete
     * @param processVariable The process variable to store the result in
     * @param result The String result to set in the complete variables
     */
    private void completeATaskWith(Task t, TodoResult result, String processVariable) {
        if (t != null) {
            Map<String, Object> properties = new HashMap<>();
            properties.put(processVariable, result.getResult());
            taskSvc.complete(t.getId(), properties);
        }
    }
    
    /**
     * Retrieves the task where the process with processInstanceId landed.
     * @param processInstanceId The String process Instance id of the process
     * @return Task object if the process landed on a next task. Null if the process
     * ended
     */
    private Task getNextTask(String processInstanceId) {
        return taskSvc.createTaskQuery().processInstanceId(processInstanceId).singleResult();
    }

    /**
     * Completes the current Task t if the task is not null and retrieves the
     * task where the process with processInstanceId landed. The task will
     * be completed with a RESULT = TRUE
     * @param t The Task to complete if necessary
     * @param processInstanceId The String process Instance id of the process
     * @return Task object if the process landed on a next task. Null if the process
     * ended
     */
    private Task completeAndGetNextTask(Task t, String processInstanceId) {
        return completeWithAndGetNextTask(t,TodoResult.SUBMIT, processInstanceId);
    }

    /**
     * Completes the current Task t if the task is not null and retrieves the
     * task where the process with processInstanceId landed. The task will
     * be completed with a RESULT = result param
     * @param t The Task to complete if necessary
     * @param result The String result to complete the task with
     * @param processInstanceId The String process Instance id of the process
     * @return Task object if the process landed on a next task. Null if the process
     * ended
     */
    private Task completeWithAndGetNextTask(Task t, TodoResult result, String processInstanceId) {
        completeATaskWith(t, result);
        return getNextTask(processInstanceId);
    }
    
    /**
     * Completes the current Task t if the task is not null and retrieves the
     * parallel tasks where the process with processInstanceId landed. The task will
     * be completed with a RESULT = TRUE
     * @param t The Task to complete if necessary
     * @param processInstanceId The String process Instance id of the process
     * @return List of Task objects if the process landed on a next task.
     */
    private List<Task> completeAndGetParallelTasks(Task t, String processInstanceId) {
        return completeWithAndGetParallelTasks(t,TodoResult.SUBMIT, processInstanceId);
    }
    
    /**
     * Completes the current Task t if the task is not null and retrieves the
     * parallel tasks where the process with processInstanceId landed. The task will
     * be completed with a RESULT = result param
     * @param t The Task to complete if necessary
     * @param result The String result to complete the task with
     * @param processInstanceId The String process Instance id of the process
     * @return List of Task objects if the process landed on a next task.
     */
    private List<Task> completeWithAndGetParallelTasks(Task t, TodoResult result, String processInstanceId) {
        completeATaskWith(t, result);
        return taskSvc.createTaskQuery().processInstanceId(processInstanceId).list();
    }
    
    /**
     * Completes the current Task t if the task is not null and retrieves the
     * task where the process with processInstanceId landed. The task will
     * be completed with a RESULT = TRUE
     * @param t The Task to complete if necessary
     * @param processInstanceId The String process Instance id of the process
     * @return Task object if the process landed on a next task. Null if the process
     * ended
     */
    private Task completeAndGetNextTask(List<Task> tasks, String processInstanceId) {
        return completeWithAndGetNextTask(tasks,TodoResult.APPROVE, processInstanceId);
    }

    /**
     * Completes the current Task t if the task is not null and retrieves the
     * task where the process with processInstanceId landed. The task will
     * be completed with a RESULT = result param
     * @param t The Task to complete if necessary
     * @param result The String result to complete the task with
     * @param processInstanceId The String process Instance id of the process
     * @return Task object if the process landed on a next task. Null if the process
     * ended
     */
    private Task completeWithAndGetNextTask(List<Task> tasks, TodoResult result, String processInstanceId) {
        if (tasks != null) {
            completeATaskWith(tasks.get(0), result, ProcessConstants.REVIEWER_RESULT_VARIABLE);
            completeATaskWith(tasks.get(1), result, ProcessConstants.REVIEWEE_RESULT_VARIABLE);
        }

        return getNextTask(processInstanceId);
    }

    /**
     * invokes assertNotNull in the passed task and assertEquals in Task.getAssignee() with the
     * assignee parameter
     * @param t The Task to assert
     * @param assignee The String value of assignee
     */
    private void assertNotNullAndAsignee(Task t, String assignee) {
        assertNotNull(t);
        assertEquals("Asignee for task "+t.getName() + " is not "+assignee+". Value = "+t.getAssignee()
            ,assignee, t.getAssignee());
    }
}
