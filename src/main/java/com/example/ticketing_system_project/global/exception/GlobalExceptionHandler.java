package com.example.ticketing_system_project.global.exception;

import com.example.ticketing_system_project.global.exception.custom.DuplicateEmailException;
import com.example.ticketing_system_project.global.exception.custom.InvalidQueueTokenException;
import com.example.ticketing_system_project.global.exception.custom.QueueTokenExpiredException;
import com.example.ticketing_system_project.global.exception.custom.WaitingInQueueException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleNotReadable(Exception e) {
        return ResponseEntity.badRequest().body(Map.of(
                "error", "JSON 파싱/DTO 매핑 실패",
                "message", e.getMessage()
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValid(MethodArgumentNotValidException e) {
        var errors = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of("field", fe.getField(), "message", fe.getDefaultMessage()))
                .toList();

        return ResponseEntity.badRequest().body(Map.of(
                "error", "Validation 실패",
                "errors", errors
        ));
    }

    /**
     * 이메일 중복 예외
     */
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException e) {
        log.error("DuplicateEmailException: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "DUPLICATE_EMAIL",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * 대기열 토큰 만료 예외
     */
    @ExceptionHandler(QueueTokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleQueueTokenExpired(QueueTokenExpiredException e) {
        log.error("QueueTokenExpiredException: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.GONE.value(),
                "QUEUE_TOKEN_EXPIRED",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.GONE).body(errorResponse);
    }

    /**
     * 잘못된 대기열 토큰 예외
     */
    @ExceptionHandler(InvalidQueueTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidQueueToken(InvalidQueueTokenException e) {
        log.error("InvalidQueueTokenException: {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_QUEUE_TOKEN",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * 대기 중 상태 예외
     */
    @ExceptionHandler(WaitingInQueueException.class)
    public ResponseEntity<WaitingErrorResponse> handleWaitingInQueue(WaitingInQueueException e) {
        log.warn("WaitingInQueueException: {}, waitingNumber: {}", e.getMessage(), e.getWaitingNumber());
        WaitingErrorResponse errorResponse = new WaitingErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "WAITING_IN_QUEUE",
                e.getMessage(),
                LocalDateTime.now(),
                e.getWaitingNumber()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * 일반적인 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unexpected exception: ", e);
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "서버 내부 오류가 발생했습니다.",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    // ErrorResponse 내부 클래스
    public record ErrorResponse(
            int status,
            String code,
            String message,
            LocalDateTime timestamp
    ) {}

    // WaitingErrorResponse 내부 클래스 (대기 순번 포함)
    public record WaitingErrorResponse(
            int status,
            String code,
            String message,
            LocalDateTime timestamp,
            Long waitingNumber
    ) {}
}