package com.hig.inovagab.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler({EmailAlreadyRegisteredException.class, DuplicateKeyException.class})
    public ResponseEntity<ApiError> handleConflict(RuntimeException ex, HttpServletRequest request) {
        String message = ex instanceof EmailAlreadyRegisteredException
                ? ex.getMessage()
                : "Registro duplicado.";
        return build(HttpStatus.CONFLICT, message, request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleBadCredentials(AuthenticationException ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.", request);
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ApiError> handleBadRequest(Exception ex, HttpServletRequest request) {
        String message = ex instanceof IllegalArgumentException
                ? ex.getMessage()
                : "Corpo da requisição inválido.";
        return build(HttpStatus.BAD_REQUEST, message, request);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message, HttpServletRequest request) {
        return ResponseEntity.status(status)
                .body(ApiError.of(status.value(), status.getReasonPhrase(), message, request.getRequestURI()));
    }
}
