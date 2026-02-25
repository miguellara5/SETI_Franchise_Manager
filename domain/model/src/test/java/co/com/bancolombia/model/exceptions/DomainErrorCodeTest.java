package co.com.bancolombia.model.exceptions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DomainErrorCodeTest {

    @Test
    void shouldReturnCodeAndMessage() {
        DomainErrorCode code = DomainErrorCode.INVALID_BRANCH;
        assertEquals("DOMAIN_001", code.getCode());
        assertEquals("Invalid branch", code.getMessage());
    }

    @Test
    void shouldReturnGenericDomainError() {
        DomainErrorCode code = DomainErrorCode.GENERIC_DOMAIN_ERROR;
        assertEquals("DOMAIN_999", code.getCode());
        assertEquals("Generic domain error", code.getMessage());
    }
}
