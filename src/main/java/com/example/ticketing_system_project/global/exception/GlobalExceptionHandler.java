package com.example.ticketing_system_project.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleNotReadable(Exception e) {
        return ResponseEntity.badRequest().body(Map.of(
                "error", "JSON 파싱/DTO 매핑 실패",
                "message", e.getMessage()
        ));
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValid(org.springframework.web.bind.MethodArgumentNotValidException e) {
        var errors = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of("field", fe.getField(), "message", fe.getDefaultMessage()))
                .toList();

        return ResponseEntity.badRequest().body(Map.of(
                "error", "Validation 실패",
                "errors", errors
        ));
    }
}