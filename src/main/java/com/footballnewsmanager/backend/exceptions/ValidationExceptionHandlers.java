package com.footballnewsmanager.backend.exceptions;

import com.footballnewsmanager.backend.api.response.auth.ArgumentNotValidResponse;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
public class ValidationExceptionHandlers {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ArgumentNotValidResponse handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ArgumentNotValidResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                "Validation Error", errors);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ConstraintViolationException.class})
    public ArgumentNotValidResponse handleConstraintExceptions(
            ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        String[] messageSplited = ex.getMessage().split(":");
        String fieldName = messageSplited[0];
        String errorMessage = messageSplited[1];
        errors.put(fieldName, errorMessage);
        return new ArgumentNotValidResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                "Constraint Error", errors);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ConversionFailedException.class})
    public ArgumentNotValidResponse handleConversionExceptions(
            ConversionFailedException ex) {
        Map<String, String> errors = new HashMap<>();
        String fieldName = ex.getMessage().split("\"")[1];
        String errorMessage = "Podany endpoint jest nieprawidłowy";
        errors.put(fieldName, errorMessage);
        return new ArgumentNotValidResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
                "Conversion Error", errors);
    }


}
