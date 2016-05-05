package com.perficient.etm.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dumbster.smtp.MailMessage;
import com.dumbster.smtp.SmtpServer;
import com.perficient.etm.domain.TodoResult;
import com.perficient.etm.service.activiti.ProcessConstants;
import com.perficient.etm.utils.SpringAppTest;

public class PeerReviewWorkflowTest extends SpringAppTest {

    private static final String PEER_REVIEW_BPM_PROCESS_NAME = "peerReview";
    
    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private SmtpServer mailServer;
    
    private Map<String, Object> getVariables() {
        Map<String, Object> variables = new HashMap<>();
        variables.put(ProcessConstants.PEER_VARIABLE, "Alex");
        variables.put(ProcessConstants.PEER_EMAIL_VARIABLE, "alex@perficient.com");
        variables.put(ProcessConstants.REVIEWEE_VARIABLE, "David Brooks");
        variables.put(ProcessConstants.FEEDBACK_VARIABLE, 1L);

        return variables;
    }

    private Map<String, Object> getResultVariableMap(TodoResult result) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(ProcessConstants.RESULT_VARIABLE, result.getResult());
        return variables;
    }
    
    @Test
    public void testHappyPath() {
        Map<String, Object> variables = getVariables();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                PEER_REVIEW_BPM_PROCESS_NAME, variables);
        assertNotNull(processInstance.getId());

        Task t = getCurrentTaskForProcess(processInstance);
        assertNotNull(t);
        assertEquals("The task to give feedback must be assigned to Author",
                t.getAssignee(),"Alex"); 
        
        taskService.complete(t.getId());
        t = getCurrentTaskForProcess(processInstance);
        assertNotNull(t);
        assertEquals("Asignee should be system user when feedback is ready", 
                t.getAssignee(), "1");
        
        taskService.complete(t.getId(),getResultVariableMap(TodoResult.APPROVE));
        t = getCurrentTaskForProcess(processInstance);
        assertNull("Process should have ended",t);
    }

    
    @Test
    public void testRejectionOfSubmittedFeedback() {
        Map<String, Object> variables = getVariables();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                PEER_REVIEW_BPM_PROCESS_NAME, variables);
        assertNotNull(processInstance.getId());

        Task t = getCurrentTaskForProcess(processInstance);
        assertNotNull(t);
        assertEquals("The task to give feedback must be assigned to Author",
                t.getAssignee(),"Alex");
        
        taskService.complete(t.getId());
        t = getCurrentTaskForProcess(processInstance);
        assertNotNull(t);
        assertEquals("Asignee should be system user when feedback is ready", 
                t.getAssignee(), "1");
        
        taskService.complete(t.getId(), getResultVariableMap(TodoResult.REJECT));
        t = getCurrentTaskForProcess(processInstance);
        assertNotNull("Process should have come back to submit feedback",t);
        assertEquals("task should be assigned to Author",t.getAssignee(), "Alex");
    }
    
    @Test
    @Ignore //Test is taking too long due to Thread.sleep. TODO: a better way: https://forums.activiti.org/content/how-do-i-unit-test-timerboundaryevent
    public void testReminderEmail() {
        Map<String, Object> variables = getVariables();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                PEER_REVIEW_BPM_PROCESS_NAME, variables);

        Task t = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertNotNull(t);

        try {
            Thread.sleep(90000);//Wait for 1.5 minutes so the time boundary of the reminder triggers.
//            taskService.complete(t.getId());

            t = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
            //An email should be triggered and the task should be back to give feedback
            //TODO check how to review the email
            assertNotNull("Task should not be null",t);
            MailMessage[] messages = smtpServer.getMessages();
            System.out.println(messages.length);
            //assertTrue("There must be messages sent",messages.length > 1);
        } catch (InterruptedException e) {
            //If the sleep method fails do not fail the test. pass it with a warning
            getLog().warn("Test email Trigger for Peer Review Workflow Failed to wait. This test will be useless");
        }
    }

    
    
    private Task getCurrentTaskForProcess(Execution processInstance){
        Task t =
                taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        return t;
    }
    
}
