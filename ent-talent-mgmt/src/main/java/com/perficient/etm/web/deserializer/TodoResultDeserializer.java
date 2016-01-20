package com.perficient.etm.web.deserializer;

import com.perficient.etm.domain.TodoResult;

public class TodoResultDeserializer extends EnumDeserializer<TodoResult> {

    private static final long serialVersionUID = 5247078831396095332L;

    public TodoResultDeserializer() {
        super(TodoResult.class);
    }
}
