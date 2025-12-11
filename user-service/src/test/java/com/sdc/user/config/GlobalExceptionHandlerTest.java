package com.sdc.user.config;

import com.sdc.user.domain.exception.BadRequestException;
import com.sdc.user.domain.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GlobalExceptionHandler
 */
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidation_SingleFieldError() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "username", "Username is required");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        // Act
        ResponseEntity<?> response = exceptionHandler.handleValidation(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("status"));
        
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) body.get("errors");
        assertEquals("Username is required", errors.get("username"));
    }

    @Test
    void handleValidation_MultipleFieldErrors() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("object", "username", "Username is required");
        FieldError fieldError2 = new FieldError("object", "email", "Email is invalid");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        // Act
        ResponseEntity<?> response = exceptionHandler.handleValidation(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) body.get("errors");
        assertEquals(2, errors.size());
        assertEquals("Username is required", errors.get("username"));
        assertEquals("Email is invalid", errors.get("email"));
    }

    @Test
    void handleMethodNotSupported() {
        // Arrange
        HttpRequestMethodNotSupportedException exception = 
            new HttpRequestMethodNotSupportedException("POST");

        // Act
        ResponseEntity<?> response = exceptionHandler.handleMethodNotSupported(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(405, body.get("status"));
        assertEquals("Request method not supported", body.get("error"));
        assertNotNull(body.get("details"));
    }

    @Test
    void handleBadRequest_BadRequestException() {
        // Arrange
        BadRequestException exception = new BadRequestException("Invalid request data");

        // Act
        ResponseEntity<?> response = exceptionHandler.handleBadRequest(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("status"));
        assertEquals("Invalid request data", body.get("error"));
    }

    @Test
    void handleBadRequest_IllegalArgumentException() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        // Act
        ResponseEntity<?> response = exceptionHandler.handleBadRequest(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("status"));
        assertEquals("Invalid argument", body.get("error"));
    }

    @Test
    void handleBadRequest_RuntimeException() {
        // Arrange
        RuntimeException exception = new RuntimeException("Runtime error");

        // Act
        ResponseEntity<?> response = exceptionHandler.handleBadRequest(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("status"));
        assertEquals("Runtime error", body.get("error"));
    }

    @Test
    void handleNotFound() {
        // Arrange
        NotFoundException exception = new NotFoundException("User not found");

        // Act
        ResponseEntity<?> response = exceptionHandler.handleNotFound(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(404, body.get("status"));
        assertEquals("User not found", body.get("error"));
    }

    @Test
    void handleValidation_EmptyFieldErrors() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        // Act
        ResponseEntity<?> response = exceptionHandler.handleValidation(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) body.get("errors");
        assertTrue(errors.isEmpty());
    }

    @Test
    void handleBadRequest_UserAlreadyExists() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("User already exists.");

        // Act
        ResponseEntity<?> response = exceptionHandler.handleBadRequest(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("User already exists.", body.get("error"));
    }

    @Test
    void handleBadRequest_EmailAlreadyInUse() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Email already in use.");

        // Act
        ResponseEntity<?> response = exceptionHandler.handleBadRequest(exception);

        // Assert
        assertNotNull(response);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Email already in use.", body.get("error"));
    }
}
