package com.example.ticketing_system_project.domain.queue.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QueueTokenResponseDto {
    private String queueToken;
    private String status;  // WAITING, ACTIVE
    private Long waitingNumber;  // 대기 순번 (WAITING 상태일 때만)

    public static QueueTokenResponseDto waiting(String queueToken, Long waitingNumber) {
        return new QueueTokenResponseDto(queueToken, "WAITING", waitingNumber);
    }

    public static QueueTokenResponseDto active(String queueToken) {
        return new QueueTokenResponseDto(queueToken, "ACTIVE", null);
    }
}