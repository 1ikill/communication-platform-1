package com.sdc.user.domain.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BadRequestException
 */
class BadRequestExceptionTest {

    @Test
    void constructor_WithMessage() {
        // Arrange
        String message = "This is a bad request";

        // Act
        BadRequestException exception = new BadRequestException(message);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void constructor_WithEmptyMessage() {
        // Arrange
        String message = "";

        // Act
        BadRequestException exception = new BadRequestException(message);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void constructor_WithNullMessage() {
        // Act
        BadRequestException exception = new BadRequestException(null);

        // Assert
        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    @Test
    void exceptionIsRuntimeException() {
        // Arrange
        BadRequestException exception = new BadRequestException("Test");

        // Assert
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void hasResponseStatusAnnotation() {
        // Act
        ResponseStatus annotation = BadRequestException.class.getAnnotation(ResponseStatus.class);

        // Assert
        assertNotNull(annotation);
        assertEquals(HttpStatus.BAD_REQUEST, annotation.code());
    }

    @Test
    void canBeThrown() {
        // Assert
        assertThrows(BadRequestException.class, () -> {
            throw new BadRequestException("Test exception");
        });
    }

    @Test
    void canBeCaught() {
        // Arrange
        String expectedMessage = "Caught exception";
        boolean caught = false;

        // Act
        try {
            throw new BadRequestException(expectedMessage);
        } catch (BadRequestException e) {
            // Assert
            assertEquals(expectedMessage, e.getMessage());
            caught = true;
        }

        assertTrue(caught, "Exception was not caught");
    }
}
