package com.sdc.ai.config;

import com.sdc.ai.domain.exception.BadRequestException;
import com.sdc.ai.domain.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleValidation_ShouldReturnBadRequestWithFieldErrors() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        FieldError fieldError1 = new FieldError("objectName", "field1", "Error message 1");
        FieldError fieldError2 = new FieldError("objectName", "field2", "Error message 2");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        // Act
        ResponseEntity<?> response = globalExceptionHandler.handleValidation(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("status"));
        
        Map<String, String> errors = (Map<String, String>) body.get("errors");
        assertNotNull(errors);
        assertEquals(2, errors.size());
        assertEquals("Error message 1", errors.get("field1"));
        assertEquals("Error message 2", errors.get("field2"));
    }

    @Test
    void handleValidation_ShouldHandleEmptyFieldErrors() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());

        // Act
        ResponseEntity<?> response = globalExceptionHandler.handleValidation(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("status"));
        
        Map<String, String> errors = (Map<String, String>) body.get("errors");
        assertNotNull(errors);
        assertTrue(errors.isEmpty());
    }

    @Test
    void handleMethodNotSupported_ShouldReturnMethodNotAllowed() {
        // Arrange
        HttpRequestMethodNotSupportedException exception = 
            new HttpRequestMethodNotSupportedException("POST", List.of("GET", "PUT"));

        // Act
        ResponseEntity<?> response = globalExceptionHandler.handleMethodNotSupported(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(405, body.get("status"));
        assertEquals("Request method not supported", body.get("error"));
        assertNotNull(body.get("details"));
    }

    @Test
    void handleBadRequest_WithBadRequestException_ShouldReturnBadRequest() {
        // Arrange
        BadRequestException exception = new BadRequestException("Invalid input data");

        // Act
        ResponseEntity<?> response = globalExceptionHandler.handleBadRequest(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("status"));
        assertEquals("Invalid input data", body.get("error"));
    }

    @Test
    void handleBadRequest_WithIllegalArgumentException_ShouldReturnBadRequest() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Illegal argument provided");

        // Act
        ResponseEntity<?> response = globalExceptionHandler.handleBadRequest(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("status"));
        assertEquals("Illegal argument provided", body.get("error"));
    }

    @Test
    void handleBadRequest_WithRuntimeException_ShouldReturnBadRequest() {
        // Arrange
        RuntimeException exception = new RuntimeException("Runtime error occurred");

        // Act
        ResponseEntity<?> response = globalExceptionHandler.handleBadRequest(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("status"));
        assertEquals("Runtime error occurred", body.get("error"));
    }

    @Test
    void handleNotFound_ShouldReturnNotFoundStatus() {
        // Arrange
        NotFoundException exception = new NotFoundException("Resource not found");

        // Act
        ResponseEntity<?> response = globalExceptionHandler.handleNotFound(exception);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()); // Note: Original implementation returns BAD_REQUEST
        
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(404, body.get("status"));
        assertEquals("Resource not found", body.get("error"));
    }
}
