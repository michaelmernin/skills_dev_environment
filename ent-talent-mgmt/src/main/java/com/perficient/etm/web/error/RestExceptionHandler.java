package com.perficient.etm.web.error;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.perficient.etm.exception.InvalidRequestException;

@ControllerAdvice(annotations = { RestController.class })
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    
    private static <E, D> List<D> mapList(List<E> list, Function<E, D> mapper) {
        return list.stream().map(mapper).collect(Collectors.toList());
    }
    
    @Inject
    private MessageSource messageSource;
    
    @ExceptionHandler({ AccessDeniedException.class })
    protected ResponseEntity<Object> handleAccessDenied(AccessDeniedException exception, WebRequest request) {
        return handleExceptionInternal(exception, null, buildHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler({ InvalidRequestException.class })
    protected ResponseEntity<Object> handleInvalidRequest(InvalidRequestException exception, WebRequest request) {
        ErrorDTO error = buildErrorDTO(exception, getGlobalErrorDTOs(exception), getFieldErrorDTOs(exception));
        return handleExceptionInternal(exception, error, buildHeaders(), HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    private ErrorDTO buildErrorDTO(InvalidRequestException exception, List<GlobalErrorDTO> globalErrors, List<FieldErrorDTO> fieldErrors) {
        ErrorDTO error = new ErrorDTO();
        error.setErrorCount(exception.getErrors().getErrorCount());
        error.setErrors(globalErrors);
        error.setFieldErrors(fieldErrors);
        return error;
    }
    
    private List<GlobalErrorDTO> getGlobalErrorDTOs(InvalidRequestException exception) {
        return mapList(exception.getErrors().getGlobalErrors(), this::mapGlobalError);
    }

    private List<FieldErrorDTO> getFieldErrorDTOs(InvalidRequestException exception) {
        return mapList(exception.getErrors().getFieldErrors(), this::mapFieldError);
    }
    
    private GlobalErrorDTO mapGlobalError(ObjectError error) {
        GlobalErrorDTO fieldError = new GlobalErrorDTO();
        fieldError.setCode(error.getCode());
        fieldError.setMessage(getMessage(error));
        return fieldError;
    }
    
    private FieldErrorDTO mapFieldError(FieldError error) {
        FieldErrorDTO fieldError = new FieldErrorDTO();
        fieldError.setField(error.getField());
        fieldError.setCode(error.getCode());
        fieldError.setMessage(getMessage(error));
        return fieldError;
    }
    
    private String getMessage(MessageSourceResolvable error) {
        return messageSource.getMessage(error, Locale.US);
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}

