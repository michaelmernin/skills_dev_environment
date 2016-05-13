package com.perficient.etm.domain;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.perficient.etm.web.deserializer.TodoResultDeserializer;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonDeserialize(using = TodoResultDeserializer.class)
public enum TodoResult {
    SUBMIT(1, true),
    APPROVE(2, true),
    REJECT(3, false);

    private static final Map<Integer, TodoResult> REGISTRY;

    static {
        REGISTRY = Collections.unmodifiableMap(Stream.of(TodoResult.values()).collect(Collectors.toMap((TodoResult rs) -> {
            return rs.getId();
        }, rs -> {
            return rs;
        }, (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        }, LinkedHashMap::new)));
    }

    public static TodoResult getById(Integer id) {
        return REGISTRY.get(id);
    }

    private Integer id;

    private boolean result;

    private TodoResult(int id, boolean result) {
        this.id = id;
        this.result = result;
    }

    public Integer getId() {
        return id;
    }

    public boolean getResult() {
        return result;
    }
}
