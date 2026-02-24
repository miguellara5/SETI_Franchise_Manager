package co.com.bancolombia.mongo.exception;

import co.com.bancolombia.model.exceptions.DomainErrorCode;

import java.util.EnumMap;
import java.util.Map;

public class DomainToAppErrorMapper {
    private static final Map<DomainErrorCode, AppErrorCode> mapping = new EnumMap<>(DomainErrorCode.class);

    static {
        mapping.put(DomainErrorCode.INVALID_FRANCHISE, AppErrorCode.VALIDATION_ERROR);
        mapping.put(DomainErrorCode.INVALID_PRODUCT, AppErrorCode.VALIDATION_ERROR);
        mapping.put(DomainErrorCode.DUPLICATE_BRANCH, AppErrorCode.DUPLICATE_BRANCH_NAME);
    }

    public static AppErrorCode map(DomainErrorCode domainCode) {
        return mapping.getOrDefault(domainCode, AppErrorCode.GENERIC_ERROR);
    }
}
