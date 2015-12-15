package com.perficient.etm.web.deserializer;

import com.perficient.etm.domain.FeedbackStatus;

public class FeedbackStatusDeserializer extends EnumDeserializer<FeedbackStatus> {

    private static final long serialVersionUID = -5499402199186654570L;

    public FeedbackStatusDeserializer() {
        super(FeedbackStatus.class);
    }
}
