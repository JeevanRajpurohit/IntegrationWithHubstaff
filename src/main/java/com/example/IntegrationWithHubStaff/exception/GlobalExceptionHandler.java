package com.example.IntegrationWithHubStaff.exception;

import com.example.IntegrationWithHubStaff.util.ResponseHandler;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseHandler> handleValidationExceptions(MethodArgumentNotValidException ex) {
        LOG.error("Validation error: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ResponseHandler response = new ResponseHandler(
                errors,
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                false,
                "errors"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseHandler> handleConstraintViolationException(ConstraintViolationException ex) {
        LOG.error("Validation error: {}", ex.getMessage());

        Map<String,String> errors= new HashMap<>();
        ex.getConstraintViolations().forEach(violation->{
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName,errorMessage);
        });

        ResponseHandler response = new ResponseHandler(
                errors,
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                false,
                "errors"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseHandler> handleRuntimeException(RuntimeException ex) {
        LOG.error("Runtime Exception: ", ex);
        ResponseHandler response = new ResponseHandler(
                null,
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                false,
                null
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
