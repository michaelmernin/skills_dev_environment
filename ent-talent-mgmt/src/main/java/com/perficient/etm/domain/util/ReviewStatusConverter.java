package com.perficient.etm.domain.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import com.perficient.etm.domain.ReviewStatus;

@Converter
public class ReviewStatusConverter implements AttributeConverter<ReviewStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ReviewStatus reviewStatus) {
        return reviewStatus.getId();
    }

    @Override
    public ReviewStatus convertToEntityAttribute(Integer id) {
        return ReviewStatus.getById(id);
    }
}
