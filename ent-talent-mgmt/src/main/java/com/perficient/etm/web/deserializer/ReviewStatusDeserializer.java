package com.perficient.etm.web.deserializer;

import com.perficient.etm.domain.ReviewStatus;

public class ReviewStatusDeserializer extends EnumDeserializer<ReviewStatus> {
    
    private static final long serialVersionUID = 4793706334651481114L;

    public ReviewStatusDeserializer() {
        super(ReviewStatus.class);
    }
}
