package com.sdc.discord.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for exception classes
 */
class ExceptionTest {

    @Test
    @DisplayName("Should create BadRequestException with message")
    void testBadRequestException() {
        String message = "Bad request error";
        BadRequestException exception = new BadRequestException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("Should create NotFoundException with message")
    void testNotFoundException() {
        String message = "Resource not found";
        NotFoundException exception = new NotFoundException(message);

        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("Should throw BadRequestException")
    void testThrowBadRequestException() {
        assertThrows(BadRequestException.class, () -> {
            throw new BadRequestException("Test error");
        });
    }

    @Test
    @DisplayName("Should throw NotFoundException")
    void testThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> {
            throw new NotFoundException("Test error");
        });
    }

    @Test
    @DisplayName("BadRequestException should extend RuntimeException")
    void testBadRequestExceptionInheritance() {
        BadRequestException exception = new BadRequestException("test");
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("NotFoundException should extend RuntimeException")
    void testNotFoundExceptionInheritance() {
        NotFoundException exception = new NotFoundException("test");
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Should create exception with null message")
    void testExceptionWithNullMessage() {
        BadRequestException badRequestException = new BadRequestException(null);
        NotFoundException notFoundException = new NotFoundException(null);

        assertNull(badRequestException.getMessage());
        assertNull(notFoundException.getMessage());
    }

    @Test
    @DisplayName("Should create exception with empty message")
    void testExceptionWithEmptyMessage() {
        String emptyMessage = "";
        BadRequestException badRequestException = new BadRequestException(emptyMessage);
        NotFoundException notFoundException = new NotFoundException(emptyMessage);

        assertEquals(emptyMessage, badRequestException.getMessage());
        assertEquals(emptyMessage, notFoundException.getMessage());
    }

    @Test
    @DisplayName("Should preserve exception message")
    void testExceptionMessagePreservation() {
        String message = "Detailed error message with special characters: !@#$%";
        BadRequestException exception = new BadRequestException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("Should create exceptions with long messages")
    void testExceptionWithLongMessage() {
        String longMessage = "a".repeat(1000);
        BadRequestException badRequestException = new BadRequestException(longMessage);
        NotFoundException notFoundException = new NotFoundException(longMessage);

        assertEquals(longMessage, badRequestException.getMessage());
        assertEquals(longMessage, notFoundException.getMessage());
    }
}
