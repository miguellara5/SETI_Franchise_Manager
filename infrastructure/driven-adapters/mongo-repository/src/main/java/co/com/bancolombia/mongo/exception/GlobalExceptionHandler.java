package co.com.bancolombia.mongo.exception;

import co.com.bancolombia.model.exceptions.DomainValidation;
import org.reactivecommons.utils.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.WebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@Order(-2)
@RequiredArgsConstructor
public class GlobalExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        AppErrorCode errorCode;
        String message;

        if (ex instanceof AppException appEx) {
            errorCode = appEx.getErrorCode();
            message = errorCode.getMessage();
        } else if (ex instanceof WebExchangeBindException bindEx) {
            errorCode = AppErrorCode.VALIDATION_ERROR;
            message = bindEx.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("Validation error");
        } else if (ex instanceof IllegalArgumentException) {
            errorCode = AppErrorCode.VALIDATION_ERROR;
            message = ex.getMessage();
        } else if (ex instanceof DomainValidation domainEx) {
            errorCode = DomainToAppErrorMapper.map(domainEx.getErrorCode());
            message = domainEx.getMessage();
        } else {
            errorCode = AppErrorCode.GENERIC_ERROR;
            message = errorCode.getMessage();
        }

        ErrorResponse response = new ErrorResponse(
                errorCode.getStatus().value(),
                errorCode.getCode(),
                message,
                exchange.getRequest().getPath().value(),
                LocalDateTime.now());
        response.setRequestId(UUID.randomUUID().toString());

        // Logging
        if (errorCode.getStatus().is4xxClientError()) {
            log.warn("[{}] Client error: {}", response.getRequestId(), ex.getMessage());
        } else {
            log.error("[{}] Server error: {}", response.getRequestId(), ex.getMessage(), ex);
        }

        // Prepare response
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().setStatusCode(errorCode.getStatus());

        byte[] bytes;
        try {
            // Note: Since ReactiveCommons ObjectMapper doesn't expose writeValueAsBytes
            // directly like Jackson,
            // we first map to Object and if needed convert, but it's easier to map the
            // ErrorResponse directly or use standard Jackson here.
            // As the user had com.fasterxml.jackson.databind.ObjectMapper, they might just
            // have had the wrong injection. Let me fix the mapping call.
            bytes = objectMapper.map(response, String.class).getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error serializing error response", e);
            bytes = "{\"message\":\"Internal error\"}".getBytes(StandardCharsets.UTF_8);
        }

        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
