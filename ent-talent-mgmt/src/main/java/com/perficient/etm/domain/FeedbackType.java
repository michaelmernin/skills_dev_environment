package com.perficient.etm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.perficient.etm.web.deserializer.FeedbackTypeDeserializer;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A FeedbackType.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonDeserialize(using = FeedbackTypeDeserializer.class)
public enum FeedbackType {
    SELF(1, "Self"),
    REVIEWER(2, "Reviewer"),
    PEER(3, "Peer");
    
    private static final Map<Integer, FeedbackType> REGISTRY;
    
    static {
        REGISTRY = Collections.unmodifiableMap(Stream.of(FeedbackType.values()).collect(Collectors.toMap((FeedbackType ft) -> {
            return ft.getId();
        }, ft -> {
            return ft;
        }, (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        }, LinkedHashMap::new)));
    }

    public static FeedbackType getById(Integer id) {
        return REGISTRY.get(id);
    }

    private Integer id;

    private String name;
    
    private FeedbackType(int id, String name) {
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
