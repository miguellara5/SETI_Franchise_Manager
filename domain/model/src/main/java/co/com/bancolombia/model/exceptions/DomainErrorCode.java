package co.com.bancolombia.model.exceptions;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DomainErrorCode {
    INVALID_BRANCH("DOMAIN_001", "Invalid branch"),
    DUPLICATE_BRANCH("DOMAIN_002", "Duplicate branch name"),
    INVALID_PRODUCT("DOMAIN_003", "Invalid product"),
    INVALID_FRANCHISE("DOMAIN_004", "Invalid franchise"),
    GENERIC_DOMAIN_ERROR("DOMAIN_999", "Generic domain error");

    private final String code;
    private final String message;
}
