package com.perficient.etm.web.rest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.perficient.etm.domain.TodoResult;

public class TodoActionDTO {
    
    @NotNull
    private TodoResult result;
    
    @NotNull
    @Size(min = 1, max = 50)
    private String todoId;
    
    public TodoActionDTO() {
    }

    public TodoResult getResult() {
        return result;
    }

    public void setResult(TodoResult result) {
        this.result = result;
    }

    public String getTodoId() {
        return todoId;
    }

    public void setTodoId(String todoId) {
        this.todoId = todoId;
    }
}
