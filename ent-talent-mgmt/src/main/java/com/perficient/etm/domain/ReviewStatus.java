package com.perficient.etm.domain;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.perficient.etm.web.deserializer.ReviewStatusDeserializer;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonDeserialize(using = ReviewStatusDeserializer.class)
public enum ReviewStatus {
    OPEN(1, "Open"),
    DIRECTOR_APPROVAL(2, "Director Approval"),
    JOINT_APPROVAL(3, "Joint Approval"),
    GM_APPROVAL(4, "GM Approval"),
    COMPLETE(5, "Complete"),
    CLOSED(6, "Closed");

    private static final Map<Integer, ReviewStatus> REGISTRY;

    static {
        REGISTRY = Collections.unmodifiableMap(Stream.of(ReviewStatus.values()).collect(Collectors.toMap((ReviewStatus rs) -> {
            return rs.getId();
        }, rs -> {
            return rs;
        }, (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        }, LinkedHashMap::new)));
    }

    public static ReviewStatus getById(Integer id) {
        return REGISTRY.get(id);
    }

    private Integer id;

    private String name;

    private ReviewStatus(int id, String name) {
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
