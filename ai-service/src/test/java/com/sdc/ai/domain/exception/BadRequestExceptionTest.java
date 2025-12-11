package com.sdc.ai.domain.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.*;

class BadRequestExceptionTest {

    @Test
    void constructor_ShouldSetMessage() {
        // Arrange
        String errorMessage = "Invalid request data";

        // Act
        BadRequestException exception = new BadRequestException(errorMessage);

        // Assert
        assertNotNull(exception);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void constructor_WithDifferentMessage_ShouldSetCorrectMessage() {
        // Arrange
        String errorMessage = "Missing required field";

        // Act
        BadRequestException exception = new BadRequestException(errorMessage);

        // Assert
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void exception_ShouldBeRuntimeException() {
        // Arrange & Act
        BadRequestException exception = new BadRequestException("Error");

        // Assert
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void exception_ShouldHaveResponseStatusAnnotation() {
        // Act
        ResponseStatus annotation = BadRequestException.class.getAnnotation(ResponseStatus.class);

        // Assert
        assertNotNull(annotation);
        assertEquals(HttpStatus.BAD_REQUEST, annotation.code());
    }

    @Test
    void constructor_WithEmptyMessage_ShouldSetEmptyMessage() {
        // Arrange
        String errorMessage = "";

        // Act
        BadRequestException exception = new BadRequestException(errorMessage);

        // Assert
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void constructor_WithNullMessage_ShouldSetNullMessage() {
        // Act
        BadRequestException exception = new BadRequestException(null);

        // Assert
        assertNull(exception.getMessage());
    }

    @Test
    void constructor_WithLongMessage_ShouldSetLongMessage() {
        // Arrange
        String longMessage = "This is a very long error message that describes in detail what went wrong with the request and provides helpful information to the developer";

        // Act
        BadRequestException exception = new BadRequestException(longMessage);

        // Assert
        assertEquals(longMessage, exception.getMessage());
    }
}
