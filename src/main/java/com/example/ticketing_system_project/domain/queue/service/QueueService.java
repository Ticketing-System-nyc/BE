package com.example.ticketing_system_project.domain.queue.service;

import com.example.ticketing_system_project.domain.queue.dto.QueuePositionResponseDto;
import com.example.ticketing_system_project.domain.queue.dto.QueueTokenResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${queue.token.active-ttl}")
    private long activeTtl;

    @Value("${queue.token.waiting-ttl}")
    private long waitingTtl;

    @Value("${queue.activation.batch-size}")
    private int batchSize;

    @Value("${queue.activation.interval}")
    private long activationInterval;

    private static final String WAITING_QUEUE_KEY = "waiting:queue:";
    private static final String ACTIVE_TOKEN_KEY = "active:token:";

    // 대기열 토큰 발급
    public QueueTokenResponseDto issueToken(Long userId, Long concertId) {
        String queueToken = UUID.randomUUID().toString();
        String waitingKey = WAITING_QUEUE_KEY + concertId;

        // 현재 시각을 score로 사용 (FIFO 보장)
        double score = System.currentTimeMillis();

        // Redis Sorted Set에 추가
        redisTemplate.opsForZSet().add(waitingKey, queueToken, score);

        // 현재 대기 순번 조회
        Long rank = redisTemplate.opsForZSet().rank(waitingKey, queueToken);
        Long waitingNumber = (rank != null) ? rank + 1 : 1L;

        log.info("대기열 토큰 발급 - userId: {}, concertId: {}, token: {}, waitingNumber: {}",
                userId, concertId, queueToken, waitingNumber);

        return QueueTokenResponseDto.waiting(queueToken, waitingNumber);
    }

    // 대기 순번 조회
    public QueuePositionResponseDto getPosition(String queueToken, Long concertId) {
        // 1. 활성 토큰인지 확인
        String activeKey = ACTIVE_TOKEN_KEY + queueToken;
        Boolean isActive = redisTemplate.hasKey(activeKey);

        if (Boolean.TRUE.equals(isActive)) {
            return QueuePositionResponseDto.active();
        }

        // 2. 대기열에서 순번 확인
        String waitingKey = WAITING_QUEUE_KEY + concertId;
        Long rank = redisTemplate.opsForZSet().rank(waitingKey, queueToken);

        if (rank == null) {
            // 토큰이 존재하지 않음 (만료됨)
            return QueuePositionResponseDto.expired();
        }

        Long waitingNumber = rank + 1;

        // 예상 대기 시간 계산 (배치 크기와 활성화 주기 기반)
        long estimatedWaitTime = (waitingNumber / batchSize) * (activationInterval / 1000);

        return QueuePositionResponseDto.waiting(waitingNumber, estimatedWaitTime);
    }

    // 토큰 검증 (인터셉터에서 사용)
    public boolean isActiveToken(String queueToken) {
        String activeKey = ACTIVE_TOKEN_KEY + queueToken;
        return Boolean.TRUE.equals(redisTemplate.hasKey(activeKey));
    }

    // 활성 토큰으로 전환 (스케줄러에서 호출)
    public void activateToken(String queueToken) {
        String activeKey = ACTIVE_TOKEN_KEY + queueToken;

        // 활성 토큰으로 저장 (TTL 설정)
        redisTemplate.opsForValue().set(activeKey, "ACTIVE", Duration.ofSeconds(activeTtl));

        log.info("토큰 활성화 - token: {}", queueToken);
    }

    // 대기열에서 토큰 제거
    public void removeFromWaitingQueue(Long concertId, String queueToken) {
        String waitingKey = WAITING_QUEUE_KEY + concertId;
        redisTemplate.opsForZSet().remove(waitingKey, queueToken);
    }

    // 대기열 토큰 삭제 (사용자가 포기한 경우)
    public void deleteToken(String queueToken, Long concertId) {
        // 대기열에서 제거
        removeFromWaitingQueue(concertId, queueToken);

        // 활성 토큰에서 제거
        String activeKey = ACTIVE_TOKEN_KEY + queueToken;
        redisTemplate.delete(activeKey);

        log.info("토큰 삭제 - token: {}, concertId: {}", queueToken, concertId);
    }

    // 대기열 상위 N명 조회 (스케줄러에서 사용)
    public java.util.Set<Object> getTopWaitingTokens(Long concertId, int count) {
        String waitingKey = WAITING_QUEUE_KEY + concertId;
        return redisTemplate.opsForZSet().range(waitingKey, 0, count - 1);
    }
}