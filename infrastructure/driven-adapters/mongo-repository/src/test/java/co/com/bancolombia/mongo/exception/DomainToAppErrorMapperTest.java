package co.com.bancolombia.mongo.exception;

import co.com.bancolombia.model.exceptions.DomainErrorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DomainToAppErrorMapperTest {

    @Test
    void shouldReturnValidationErrorForInvalidFranchise() {
        AppErrorCode result = DomainToAppErrorMapper.map(DomainErrorCode.INVALID_FRANCHISE);
        assertEquals(AppErrorCode.VALIDATION_ERROR, result);
    }

    @Test
    void shouldReturnValidationErrorForInvalidProduct() {
        AppErrorCode result = DomainToAppErrorMapper.map(DomainErrorCode.INVALID_PRODUCT);
        assertEquals(AppErrorCode.VALIDATION_ERROR, result);
    }

    @Test
    void shouldReturnDuplicateBranchNameForDuplicateBranch() {
        AppErrorCode result = DomainToAppErrorMapper.map(DomainErrorCode.DUPLICATE_BRANCH);
        assertEquals(AppErrorCode.DUPLICATE_BRANCH_NAME, result);
    }
}