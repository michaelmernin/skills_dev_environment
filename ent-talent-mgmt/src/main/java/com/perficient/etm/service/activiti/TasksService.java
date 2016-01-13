package com.perficient.etm.service.activiti;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private TaskService taskSvc;

    /**
     * Retrieve the tasks associated with a specific user. This can be
     * considered as retrieving the to-do list of the user
     *
     * @param user
     *            The String user id to check
     * @return List of String objects with the ids of the tasks
     */
    public List<Task> getTasks(String user) {
        List<Task> tasks = taskSvc.createTaskQuery().taskAssignee(user).list();
        return tasks;
    }

    /**
     * Retrieves a specific task from the Activiti engine based on the specified
     * task Id
     *
     * @param activitiTaskId
     *            The String id of the task to be retrieved from the engine
     * @return The String id of the Task in the Activiti engine
     */
    public String getTask(String activitiTaskId) {
        Task task = taskSvc.createTaskQuery().taskId(activitiTaskId)
                .singleResult();

        return (task != null) ? task.getId() : null;
    }

    /**
     * Completes one task in the Activiti engine in order to allow the process
     * to continue.
     *
     * @param activitiTaskId
     *            the String id of the Task in the activiti engine
     * @param result
     *            The String result of the task that will be used to continue in
     *            the workflow process.
     */
    public void complete(String activitiTaskId, String result) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("RESULT", result);
        taskSvc.complete(activitiTaskId, variables);
    }

    /**
     * Retrieves the Current tasks that are being executed in the process
     *
     * @param processInstanceId
     *            The String process instance id as marked in the activiti
     *            engine
     * @return List of String objects with the ids of the tasks
     */
    public List<String> getProcessTasks(String processInstanceId) {
        List<Task> tasks = taskSvc.createTaskQuery()
                .processInstanceId(processInstanceId).list();

        List<String> ids = tasks.stream().map(Task::getId)
                .collect(Collectors.toList());
        return ids;
    }
}
