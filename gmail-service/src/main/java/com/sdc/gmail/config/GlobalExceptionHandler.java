package com.sdc.gmail.config;

import com.sdc.gmail.domain.exception.BadRequestException;
import com.sdc.gmail.domain.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler.
 * @since 11.2025
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "status", 400,
                        "errors", errors
                ));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Map.of(
                        "status", 405,
                        "error", "Request method not supported",
                        "details", ex.getMessage()
                ));
    }

    @ExceptionHandler({
            BadRequestException.class,
            IllegalArgumentException.class,
            RuntimeException.class,
            IOException.class
    })
    public ResponseEntity<?> handleBadRequest(RuntimeException ex) {

        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "status", 400,
                        "error", ex.getMessage()
                ));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFound(RuntimeException ex) {
        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "status", 404,
                        "error", ex.getMessage()
                ));
    }
}
