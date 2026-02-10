package com.example.ticketing_system_project.global.exception.custom;

public class InvalidQueueTokenException extends RuntimeException {
    public InvalidQueueTokenException(String message) {
        super(message);
    }
}