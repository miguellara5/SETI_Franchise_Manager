package co.com.bancolombia.mongo.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppExceptionTest {

    @Test
    void shouldCreateExceptionWithErrorCode() {
        AppException exception = new AppException(AppErrorCode.VALIDATION_ERROR);

        assertEquals(AppErrorCode.VALIDATION_ERROR, exception.getErrorCode());
        assertEquals("Error validating request", exception.getMessage());
        assertNotNull(exception.getArgs());
        assertEquals(0, exception.getArgs().length);
    }

    @Test
    void shouldCreateExceptionWithCustomMessage() {
        String customMessage = "Custom validation failed";
        AppException exception = new AppException(AppErrorCode.VALIDATION_ERROR, customMessage);

        assertEquals(AppErrorCode.VALIDATION_ERROR, exception.getErrorCode());
        assertEquals(customMessage, exception.getMessage());
    }

    @Test
    void shouldCreateExceptionWithCause() {
        Throwable cause = new RuntimeException("Root cause");
        AppException exception = new AppException(AppErrorCode.DATABASE_ERROR, cause);

        assertEquals(AppErrorCode.DATABASE_ERROR, exception.getErrorCode());
        assertEquals("Database error", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void shouldCreateExceptionWithCustomMessageAndCause() {
        Throwable cause = new RuntimeException("DB exception");
        String message = "Could not connect to DB";
        AppException exception = new AppException(AppErrorCode.DATABASE_ERROR, message, cause);

        assertEquals(AppErrorCode.DATABASE_ERROR, exception.getErrorCode());
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void shouldCreateExceptionWithArgs() {
        AppException exception = new AppException(AppErrorCode.GENERIC_ERROR, "arg1", 42);

        assertEquals(AppErrorCode.GENERIC_ERROR, exception.getErrorCode());
        assertEquals("Internal server error", exception.getMessage()); // mensaje sin formato
        assertArrayEquals(new Object[]{"arg1", 42}, exception.getArgs());
    }
}