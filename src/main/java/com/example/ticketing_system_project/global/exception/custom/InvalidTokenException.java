package com.example.ticketing_system_project.global.exception.custom;

public class InvalidTokenException extends RuntimeException { // JWT 인증 토큰 검증 실패
    public InvalidTokenException(String message) {
        super(message);
    }
}