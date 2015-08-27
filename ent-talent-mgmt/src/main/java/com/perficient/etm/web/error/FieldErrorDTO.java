package com.perficient.etm.web.error;

public class FieldErrorDTO extends GlobalErrorDTO {
    
    private String field;
    
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

}
