package com.perficient.etm.domain.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.perficient.etm.domain.FeedbackType;

@Converter
public class FeedbackTypeConverter implements AttributeConverter<FeedbackType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(FeedbackType feedbackStatus) {
        return feedbackStatus.getId();
    }

    @Override
    public FeedbackType convertToEntityAttribute(Integer id) {
        return FeedbackType.getById(id);
    }
}
