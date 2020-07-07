package com.example.myshop.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(CustomExceptionHandler.class.getName());

    @ExceptionHandler(value = {ResponseException.class})
    public ResponseEntity<ResponseException> handleExceptionResponse(Exception cause) {
        LOGGER.log(Level.SEVERE, "Error response sent", cause);
        ResponseException ex;
        if (cause instanceof ResponseException) {
            ex = (ResponseException) cause;
        } else {
            ex = new ResponseException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal error occurred", cause);
        }
        return new ResponseEntity<>(ex, ex.getHttpStatus());
    }
}
