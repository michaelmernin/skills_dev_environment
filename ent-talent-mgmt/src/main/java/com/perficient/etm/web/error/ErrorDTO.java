package com.perficient.etm.web.error;

import java.util.List;

public class ErrorDTO {

    private int errorCount;

    private List<GlobalErrorDTO> errors;

    private List<FieldErrorDTO> fieldErrors;

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public List<GlobalErrorDTO> getErrors() {
        return errors;
    }

    public void setErrors(List<GlobalErrorDTO> errors) {
        this.errors = errors;
    }

    public List<FieldErrorDTO> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(List<FieldErrorDTO> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

}
