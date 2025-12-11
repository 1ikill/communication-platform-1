package com.sdc.ai.domain.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.*;

class NotFoundExceptionTest {

    @Test
    void constructor_ShouldSetMessage() {
        // Arrange
        String errorMessage = "Resource not found";

        // Act
        NotFoundException exception = new NotFoundException(errorMessage);

        // Assert
        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void constructor_WithDifferentMessage_ShouldSetCorrectMessage() {
        // Arrange
        String errorMessage = "Contact profile not found";

        // Act
        NotFoundException exception = new NotFoundException(errorMessage);

        // Assert
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void exception_ShouldBeRuntimeException() {
        // Arrange & Act
        NotFoundException exception = new NotFoundException("Error");

        // Assert
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void exception_ShouldHaveResponseStatusAnnotation() {
        // Act
        ResponseStatus annotation = NotFoundException.class.getAnnotation(ResponseStatus.class);

        // Assert
        assertNotNull(annotation);
        assertEquals(HttpStatus.NOT_FOUND, annotation.code());
    }

    @Test
    void constructor_WithEmptyMessage_ShouldSetEmptyMessage() {
        // Arrange
        String errorMessage = "";

        // Act
        NotFoundException exception = new NotFoundException(errorMessage);

        // Assert
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void constructor_WithNullMessage_ShouldSetNullMessage() {
        // Act
        NotFoundException exception = new NotFoundException(null);

        // Assert
        assertNull(exception.getMessage());
    }

    @Test
    void constructor_WithIdInMessage_ShouldSetMessageWithId() {
        // Arrange
        String errorMessage = "User with id 123 not found";

        // Act
        NotFoundException exception = new NotFoundException(errorMessage);

        // Assert
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void constructor_WithLongMessage_ShouldSetLongMessage() {
        // Arrange
        String longMessage = "The requested resource could not be found in the database. Please verify the identifier and try again.";

        // Act
        NotFoundException exception = new NotFoundException(longMessage);

        // Assert
        assertEquals(longMessage, exception.getMessage());
    }
}
