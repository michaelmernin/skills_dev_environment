package com.perficient.etm.web.validator;

import org.springframework.core.GenericTypeResolver;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public abstract class DomainValidator<D> implements Validator {

    private final Class<D> domainType;

    @SuppressWarnings("unchecked")
    public DomainValidator() {
        domainType = (Class<D>) GenericTypeResolver.resolveTypeArgument(getClass(), DomainValidator.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void validate(Object target, Errors errors) {
        validateDomain((D) target, errors);
    }
    
    protected abstract void validateDomain(D domain, Errors errors);
    
    @Override
    public boolean supports(Class<?> clazz) {
        return domainType.isAssignableFrom(clazz);
    }

}
