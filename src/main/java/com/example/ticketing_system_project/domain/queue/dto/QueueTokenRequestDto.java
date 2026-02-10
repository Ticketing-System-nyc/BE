package com.example.ticketing_system_project.domain.queue.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QueueTokenRequestDto {
    @NotNull(message = "콘서트 ID는 필수입니다")
    private Long concertId;
}