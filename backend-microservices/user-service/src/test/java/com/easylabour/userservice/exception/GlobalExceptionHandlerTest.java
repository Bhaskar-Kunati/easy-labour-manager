package com.easylabour.userservice.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");
        ResponseEntity<Object> response = exceptionHandler.handleBadRequestExceptions(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleIllegalStateException() {
        IllegalStateException ex = new IllegalStateException("Duplicate state");
        ResponseEntity<Object> response = exceptionHandler.handleBadRequestExceptions(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleNotFoundRuntimeException() {
        RuntimeException ex = new RuntimeException("Worker profile not found");
        ResponseEntity<Object> response = exceptionHandler.handleRuntimeException(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void handleGeneralRuntimeException() {
        RuntimeException ex = new RuntimeException("Some error");
        ResponseEntity<Object> response = exceptionHandler.handleRuntimeException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void handleGeneralException() {
        Exception ex = new Exception("Unexpected");
        ResponseEntity<Object> response = exceptionHandler.handleGeneralException(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
