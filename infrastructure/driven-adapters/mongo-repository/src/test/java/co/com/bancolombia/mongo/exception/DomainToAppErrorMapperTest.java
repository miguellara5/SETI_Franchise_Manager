package co.com.bancolombia.mongo.exception;

import co.com.bancolombia.model.exceptions.DomainErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class DomainToAppErrorMapperTest {

    @Test
    void shouldReturnValidationErrorForInvalidFranchise() {
        AppErrorCode result = DomainToAppErrorMapper.map(DomainErrorCode.INVALID_FRANCHISE);
        assertEquals(AppErrorCode.VALIDATION_ERROR, result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        assertEquals("VALIDATION_001", result.getCode());
    }

    @Test
    void shouldReturnValidationErrorForInvalidProduct() {
        AppErrorCode result = DomainToAppErrorMapper.map(DomainErrorCode.INVALID_PRODUCT);
        assertEquals(AppErrorCode.VALIDATION_ERROR, result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
    }

    @Test
    void shouldReturnDuplicateBranchNameForDuplicateBranch() {
        AppErrorCode result = DomainToAppErrorMapper.map(DomainErrorCode.DUPLICATE_BRANCH);
        assertEquals(AppErrorCode.DUPLICATE_BRANCH_NAME, result);
        assertEquals(HttpStatus.CONFLICT, result.getStatus());
        assertEquals("BRANCH_002", result.getCode());
    }

    @Test
    void shouldReturnGenericErrorForNullErrorCode() {
        AppErrorCode result = DomainToAppErrorMapper.map(null);
        assertEquals(AppErrorCode.GENERIC_ERROR, result);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatus());
        assertEquals("SYSTEM_001", result.getCode());
    }

    @Test
    void shouldMapAllDomainErrorCodesToValidAppErrorCode() {
        for (DomainErrorCode errorCode : DomainErrorCode.values()) {
            AppErrorCode result = DomainToAppErrorMapper.map(errorCode);
            assertNotNull(result, "Mapped AppErrorCode should not be null for: " + errorCode);
            assertNotNull(result.getStatus(), "Status should not be null for " + errorCode);
            assertNotNull(result.getCode(), "Code should not be null for " + errorCode);
            assertNotNull(result.getMessage(), "Message should not be null for " + errorCode);
        }
    }

    @Test
    void shouldHaveValidStatusForMappedError() {
        AppErrorCode result = DomainToAppErrorMapper.map(DomainErrorCode.INVALID_FRANCHISE);
        assertNotNull(result.getStatus());
        assertTrue(result.getStatus() == HttpStatus.BAD_REQUEST || 
                   result.getStatus() == HttpStatus.CONFLICT || 
                   result.getStatus() == HttpStatus.NOT_FOUND ||
                   result.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void shouldHaveValidMessageForMappedError() {
        AppErrorCode result = DomainToAppErrorMapper.map(DomainErrorCode.INVALID_PRODUCT);
        assertNotNull(result.getMessage());
        assertFalse(result.getMessage().isEmpty());
    }

    @Test
    void shouldMapAllErrorCodesConsistently() {
        DomainErrorCode errorCode = DomainErrorCode.INVALID_FRANCHISE;
        AppErrorCode result1 = DomainToAppErrorMapper.map(errorCode);
        AppErrorCode result2 = DomainToAppErrorMapper.map(errorCode);
        
        assertEquals(result1, result2);
        assertEquals(result1.getCode(), result2.getCode());
    }
}