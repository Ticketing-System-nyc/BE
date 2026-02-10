package com.example.ticketing_system_project.global.exception.custom;

public class WaitingInQueueException extends RuntimeException {
    private final Long waitingNumber;

    public WaitingInQueueException(String message, Long waitingNumber) {
        super(message);
        this.waitingNumber = waitingNumber;
    }

    public Long getWaitingNumber() {
        return waitingNumber;
    }
}