package com.perficient.etm.web.deserializer;

import com.perficient.etm.domain.FeedbackType;

public class FeedbackTypeDeserializer extends EnumDeserializer<FeedbackType> {

    private static final long serialVersionUID = -8502983335403996200L;

    public FeedbackTypeDeserializer() {
        super(FeedbackType.class);
    }
}
