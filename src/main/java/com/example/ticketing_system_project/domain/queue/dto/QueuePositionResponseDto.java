package com.example.ticketing_system_project.domain.queue.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QueuePositionResponseDto {
    private String status;  // WAITING, ACTIVE, EXPIRED
    private Long waitingNumber;  // 내 앞 대기 인원
    private Long estimatedWaitTime;  // 예상 대기 시간 (초)

    public static QueuePositionResponseDto waiting(Long waitingNumber, Long estimatedWaitTime) {
        return new QueuePositionResponseDto("WAITING", waitingNumber, estimatedWaitTime);
    }

    public static QueuePositionResponseDto active() {
        return new QueuePositionResponseDto("ACTIVE", 0L, 0L);
    }

    public static QueuePositionResponseDto expired() {
        return new QueuePositionResponseDto("EXPIRED", null, null);
    }
}