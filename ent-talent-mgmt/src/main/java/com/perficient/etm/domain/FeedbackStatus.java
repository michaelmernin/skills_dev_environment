package com.perficient.etm.domain;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.perficient.etm.web.deserializer.FeedbackStatusDeserializer;

/**
 * A FeedbackStatus.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonDeserialize(using = FeedbackStatusDeserializer.class)
public enum FeedbackStatus {
    NOT_SENT(1, "Not Sent"),
    OPEN(2, "Open"),
    READY(3, "Ready"),
    COMPLETE(4, "Complete"),
    CLOSED(5, "Closed");
    
    private static final Map<Integer, FeedbackStatus> REGISTRY;
    
    static {
        REGISTRY = Collections.unmodifiableMap(Stream.of(FeedbackStatus.values()).collect(Collectors.toMap(fs -> {
            return fs.getId();
        }, fs -> {
            return fs;
        }, (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        }, LinkedHashMap::new)));
    }

    public static FeedbackStatus getById(Integer id) {
        return REGISTRY.get(id);
    }

    private Integer id;

    private String name;
    
    private FeedbackStatus(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
