package com.example.ticketing_system_project.domain.queue.controller;

import com.example.ticketing_system_project.domain.queue.dto.QueuePositionResponseDto;
import com.example.ticketing_system_project.domain.queue.dto.QueueTokenRequestDto;
import com.example.ticketing_system_project.domain.queue.dto.QueueTokenResponseDto;
import com.example.ticketing_system_project.domain.queue.service.QueueService;
import com.example.ticketing_system_project.global.exception.custom.InvalidQueueTokenException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    /**
     * 대기열 토큰 발급
     * POST /api/queue/token
     */
    @PostMapping("/token")
    public ResponseEntity<QueueTokenResponseDto> issueToken(
            @Valid @RequestBody QueueTokenRequestDto requestDto
    ) {
        // 임시: 하드코딩된 userId 사용 (테스트용)
        Long userId = 1L;

        log.info("대기열 토큰 발급 요청 - userId: {}, concertId: {}", userId, requestDto.getConcertId());

        QueueTokenResponseDto response = queueService.issueToken(userId, requestDto.getConcertId());

        return ResponseEntity.ok(response);
    }

    /**
     * 대기 순번 조회
     * GET /api/queue/position?concertId={concertId}
     * Header: X-Queue-Token: {queueToken}
     */
    @GetMapping("/position")
    public ResponseEntity<QueuePositionResponseDto> getPosition(
            @RequestParam Long concertId,
            @RequestHeader(value = "X-Queue-Token", required = false) String queueToken
    ) {
        if (queueToken == null || queueToken.isBlank()) {
            throw new InvalidQueueTokenException("대기열 토큰이 없습니다.");
        }

        log.info("대기 순번 조회 - concertId: {}, queueToken: {}", concertId, queueToken);

        QueuePositionResponseDto response = queueService.getPosition(queueToken, concertId);

        return ResponseEntity.ok(response);
    }

    /**
     * 대기열 토큰 삭제 (대기 포기)
     * DELETE /api/queue/token?concertId={concertId}
     * Header: X-Queue-Token: {queueToken}
     */
    @DeleteMapping("/token")
    public ResponseEntity<Void> deleteToken(
            @RequestParam Long concertId,
            @RequestHeader(value = "X-Queue-Token", required = false) String queueToken
    ) {
        if (queueToken == null || queueToken.isBlank()) {
            throw new InvalidQueueTokenException("대기열 토큰이 없습니다.");
        }

        log.info("대기열 토큰 삭제 - concertId: {}, queueToken: {}", concertId, queueToken);

        queueService.deleteToken(queueToken, concertId);

        return ResponseEntity.noContent().build();
    }
}