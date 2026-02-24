package co.com.bancolombia.mongo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum AppErrorCode {
    FRANCHISE_NOT_FOUND(HttpStatus.NOT_FOUND, "FRANCHISE_001", "Franchise not found"),
    DUPLICATE_FRANCHISE_NAME(HttpStatus.CONFLICT, "FRANCHISE_002", "Duplicate franchise name"),
    BRANCH_NOT_FOUND(HttpStatus.NOT_FOUND, "BRANCH_001", "Branch not found"),
    DUPLICATE_BRANCH_NAME(HttpStatus.CONFLICT, "BRANCH_002", "Duplicate branch name"),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT_001", "Product not found"),
    DUPLICATE_PRODUCT_NAME(HttpStatus.CONFLICT, "PRODUCT_002", "Duplicate product name"),

    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALIDATION_001", "Error validating request"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "REQUEST_001", "Invalid request"),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB_001", "Database error"),
    GENERIC_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SYSTEM_001", "Internal server error"),

    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
