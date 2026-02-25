package co.com.bancolombia.model.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DomainValidationTest {

    @Test
    void shouldCreateWithErrorCode() {
        DomainValidation ex = new DomainValidation(DomainErrorCode.INVALID_BRANCH);
        assertEquals(DomainErrorCode.INVALID_BRANCH, ex.getErrorCode());
        assertEquals("Invalid branch", ex.getMessage());
    }

    @Test
    void shouldCreateWithCustomMessage() {
        DomainValidation ex = new DomainValidation(DomainErrorCode.INVALID_PRODUCT, "Custom error");
        assertEquals(DomainErrorCode.INVALID_PRODUCT, ex.getErrorCode());
        assertEquals("Custom error", ex.getMessage());
    }

    @Test
    void shouldCreateWithCause() {
        Throwable cause = new RuntimeException("root cause");
        DomainValidation ex = new DomainValidation(DomainErrorCode.INVALID_FRANCHISE, cause);
        assertEquals(DomainErrorCode.INVALID_FRANCHISE, ex.getErrorCode());
        assertEquals("Invalid franchise", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void shouldCreateWithCustomMessageAndCause() {
        Throwable cause = new RuntimeException("root cause");
        DomainValidation ex = new DomainValidation(DomainErrorCode.DUPLICATE_BRANCH, "Duplicate", cause);
        assertEquals(DomainErrorCode.DUPLICATE_BRANCH, ex.getErrorCode());
        assertEquals("Duplicate", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}
