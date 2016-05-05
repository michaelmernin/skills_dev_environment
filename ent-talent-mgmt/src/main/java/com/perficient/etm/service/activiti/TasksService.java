package com.perficient.etm.service.activiti;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.perficient.etm.domain.TodoResult;

/**
 * Service to manage tasks in the Activiti engine.
 *
 * @author Alexandro Blanco <alex.blanco@perficient.com>
 */
@Service
public class TasksService {

    /**
     * Connection with the TaskService service inside the Activiti engine
     */
    @Autowired
    private TaskService tasksService;

    /**
     * Retrieve the tasks associated with a specific user. This can be
     * considered as retrieving the to-do list of the user
     *
     * @param userId
     *            The String user id to check
     * @return List of String objects with the ids of the tasks
     */
    public List<Task> getUserTasks(Long userId) {
        return getCurrentUserTaskQuery(userId).list();
    }
    
    /**
     * Retrieves the Current tasks that are being executed in the process
     *
     * @param processId
     *            The String process instance id as marked in the activiti
     *            engine
     * @return List of String objects with the ids of the tasks
     */
    public Task getProcessUserTask(String processId, Long userId) {
        return getCurrentUserTaskQuery(userId).processInstanceId(processId).singleResult();
    }
    
    public List<Task> getProcessTasks(String processId) {
        return getTaskQuery().processInstanceId(processId).list();
    }

    private TaskQuery getCurrentUserTaskQuery(Long userId) {
        return getTaskQuery().taskAssignee(String.valueOf(userId));
    }
    
    private TaskQuery getTaskQuery() {
        return tasksService.createTaskQuery().active().includeProcessVariables();
    }

    /**
     * Completes one task in the Activiti engine in order to allow the process
     * to continue.
     *
     * @param taskId
     *            the String id of the Task in the activiti engine
     * @param result
     *            The String result of the task that will be used to continue in
     *            the workflow process.
     */
    public void complete(String taskId, TodoResult result) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(ProcessConstants.RESULT_VARIABLE, result.getResult());
        tasksService.complete(taskId, variables);
    }
}
