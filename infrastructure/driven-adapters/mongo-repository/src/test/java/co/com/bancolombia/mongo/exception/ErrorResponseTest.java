package co.com.bancolombia.mongo.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void shouldCreateErrorResponseWithAllFields() {
        LocalDateTime now = LocalDateTime.now();

        ErrorResponse response = new ErrorResponse(
                400,
                "VALIDATION_ERROR",
                "Invalid input data",
                "/api/test",
                now
        );
        response.setRequestId("REQ123");

        assertEquals(400, response.getStatus());
        assertEquals("VALIDATION_ERROR", response.getCode());
        assertEquals("Invalid input data", response.getMessage());
        assertEquals("/api/test", response.getPath());
        assertEquals(now, response.getTimestamp());
        assertEquals("REQ123", response.getRequestId());
    }

    @Test
    void shouldSerializeToJsonCorrectly() throws JsonProcessingException {
        LocalDateTime now = LocalDateTime.of(2025, 6, 29, 14, 30, 0);
        ErrorResponse response = new ErrorResponse(
                404,
                "NOT_FOUND",
                "Resource not found",
                "/api/resource",
                now
        );
        response.setRequestId("REQ999");

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        String json = mapper.writeValueAsString(response);

        assertTrue(json.contains("\"status\":404"));
        assertTrue(json.contains("\"code\":\"NOT_FOUND\""));
        assertTrue(json.contains("\"timestamp\":\"2025-06-29T14:30:00\""));
    }

    @Test
    void shouldNotIncludeNullFieldsInJson() throws JsonProcessingException {
        ErrorResponse response = new ErrorResponse();
        response.setStatus(500);
        response.setCode("INTERNAL_ERROR");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(response);

        assertTrue(json.contains("\"status\":500"));
        assertTrue(json.contains("\"code\":\"INTERNAL_ERROR\""));
        assertFalse(json.contains("message"));
        assertFalse(json.contains("path"));
        assertFalse(json.contains("timestamp"));
        assertFalse(json.contains("requestId"));
    }
}
