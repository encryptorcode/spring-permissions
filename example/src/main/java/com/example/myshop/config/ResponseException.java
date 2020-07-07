package com.example.myshop.config;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class ResponseException extends Exception {
    private final HttpStatus httpStatus;

    public ResponseException(HttpStatus status, String message) {
        this(status, message, null);
    }

    public ResponseException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = status;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public Map<String, String> response() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", this.getMessage());
        response.put("code", this.httpStatus.name());
        return response;
    }
}
