package com.example.ticketing_system_project.global.exception.custom;

public class QueueTokenExpiredException extends RuntimeException { // 대기열 토큰 검증 실패
    public QueueTokenExpiredException(String message) {
        super(message);
    }
}