package com.example.ticketing_system_project.global.exception.custom;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
