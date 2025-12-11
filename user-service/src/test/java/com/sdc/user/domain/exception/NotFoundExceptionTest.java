package com.sdc.user.domain.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NotFoundException
 */
class NotFoundExceptionTest {

    @Test
    void constructor_WithMessage() {
        // Arrange
        String message = "Resource not found";

        // Act
        NotFoundException exception = new NotFoundException(message);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void constructor_WithEmptyMessage() {
        // Arrange
        String message = "";

        // Act
        NotFoundException exception = new NotFoundException(message);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void constructor_WithNullMessage() {
        // Act
        NotFoundException exception = new NotFoundException(null);

        // Assert
        assertNotNull(exception);
        assertNull(exception.getMessage());
    }

    @Test
    void exceptionIsRuntimeException() {
        // Arrange
        NotFoundException exception = new NotFoundException("Test");

        // Assert
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void hasResponseStatusAnnotation() {
        // Act
        ResponseStatus annotation = NotFoundException.class.getAnnotation(ResponseStatus.class);

        // Assert
        assertNotNull(annotation);
        assertEquals(HttpStatus.NOT_FOUND, annotation.code());
    }

    @Test
    void canBeThrown() {
        // Assert
        assertThrows(NotFoundException.class, () -> {
            throw new NotFoundException("Test exception");
        });
    }

    @Test
    void canBeCaught() {
        // Arrange
        String expectedMessage = "User not found with id=1";
        boolean caught = false;

        // Act
        try {
            throw new NotFoundException(expectedMessage);
        } catch (NotFoundException e) {
            // Assert
            assertEquals(expectedMessage, e.getMessage());
            caught = true;
        }

        assertTrue(caught, "Exception was not caught");
    }

    @Test
    void differentMessagesProduceDifferentExceptions() {
        // Arrange
        NotFoundException exception1 = new NotFoundException("Message 1");
        NotFoundException exception2 = new NotFoundException("Message 2");

        // Assert
        assertNotEquals(exception1.getMessage(), exception2.getMessage());
    }

    @Test
    void canBeCaughtAsRuntimeException() {
        // Arrange
        String expectedMessage = "Test not found";
        boolean caught = false;

        // Act
        try {
            throw new NotFoundException(expectedMessage);
        } catch (RuntimeException e) {
            // Assert
            assertEquals(expectedMessage, e.getMessage());
            assertTrue(e instanceof NotFoundException);
            caught = true;
        }

        assertTrue(caught, "Exception was not caught");
    }
}
