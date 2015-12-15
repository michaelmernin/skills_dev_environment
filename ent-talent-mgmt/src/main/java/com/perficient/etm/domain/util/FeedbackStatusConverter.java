package com.perficient.etm.domain.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.perficient.etm.domain.FeedbackStatus;

@Converter
public class FeedbackStatusConverter implements AttributeConverter<FeedbackStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(FeedbackStatus feedbackStatus) {
        return feedbackStatus.getId();
    }

    @Override
    public FeedbackStatus convertToEntityAttribute(Integer id) {
        return FeedbackStatus.getById(id);
    }
}
