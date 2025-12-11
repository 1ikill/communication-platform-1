package com.sdc.gmail.config;

import com.sdc.gmail.domain.exception.BadRequestException;
import com.sdc.gmail.domain.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleValidation() {
        final MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        final BindingResult bindingResult = mock(BindingResult.class);
        final FieldError fieldError = new FieldError("object", "field", "error message");
        
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        
        final ResponseEntity<?> response = handler.handleValidation(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        final Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(400, body.get("status"));
        assertNotNull(body.get("errors"));
    }

    @Test
    void testHandleMethodNotSupported() {
        final HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("POST");
        
        final ResponseEntity<?> response = handler.handleMethodNotSupported(ex);
        
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        final Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(405, body.get("status"));
        assertEquals("Request method not supported", body.get("error"));
    }

    @Test
    void testHandleBadRequestException() {
        final BadRequestException ex = new BadRequestException("Invalid request");
        
        final ResponseEntity<?> response = handler.handleBadRequest(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        final Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(400, body.get("status"));
        assertEquals("Invalid request", body.get("error"));
    }

    @Test
    void testHandleIllegalArgumentException() {
        final IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");
        
        final ResponseEntity<?> response = handler.handleBadRequest(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        final Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(400, body.get("status"));
        assertEquals("Invalid argument", body.get("error"));
    }

    @Test
    void testHandleRuntimeException() {
        final RuntimeException ex = new RuntimeException("Runtime error");
        
        final ResponseEntity<?> response = handler.handleBadRequest(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        final Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(400, body.get("status"));
        assertEquals("Runtime error", body.get("error"));
    }

    @Test
    void testHandleIOException() {
        final RuntimeException ex = new RuntimeException("IO error");
        
        final ResponseEntity<?> response = handler.handleBadRequest(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        final Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(400, body.get("status"));
        assertEquals("IO error", body.get("error"));
    }

    @Test
    void testHandleNotFoundException() {
        final NotFoundException ex = new NotFoundException("Not found");
        
        final ResponseEntity<?> response = handler.handleNotFound(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        final Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(404, body.get("status"));
        assertEquals("Not found", body.get("error"));
    }
}
