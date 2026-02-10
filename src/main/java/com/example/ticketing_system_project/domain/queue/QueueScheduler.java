package com.example.ticketing_system_project.domain.queue;

import com.example.ticketing_system_project.domain.queue.service.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueScheduler {

    private final QueueService queueService;

    @Value("${queue.activation.batch-size}")
    private int batchSize;

    // TODO: 실제 운영 시에는 콘서트 목록을 DB에서 조회해야 함
    // 현재는 테스트용으로 concertId = 1로 고정
    private static final Long TEST_CONCERT_ID = 1L;

    /**
     * 대기열 활성화 스케줄러
     * 10초마다 실행 (queue.activation.interval 설정값)
     */
    @Scheduled(fixedDelayString = "${queue.activation.interval}")
    public void activateWaitingUsers() {
        log.info("=== 대기열 활성화 스케줄러 시작 ===");

        try {
            // 대기열 상위 N명 조회
            Set<Object> topWaitingTokens = queueService.getTopWaitingTokens(TEST_CONCERT_ID, batchSize);

            if (topWaitingTokens.isEmpty()) {
                log.info("대기 중인 사용자가 없습니다.");
                return;
            }

            log.info("활성화할 토큰 수: {}", topWaitingTokens.size());

            // 각 토큰을 활성 상태로 전환
            int activatedCount = 0;
            for (Object token : topWaitingTokens) {
                String queueToken = (String) token;

                try {
                    // 활성 토큰으로 전환
                    queueService.activateToken(queueToken);

                    // 대기열에서 제거
                    queueService.removeFromWaitingQueue(TEST_CONCERT_ID, queueToken);

                    activatedCount++;
                } catch (Exception e) {
                    log.error("토큰 활성화 실패 - token: {}, error: {}", queueToken, e.getMessage());
                }
            }

            log.info("대기열 활성화 완료 - 활성화된 토큰 수: {}/{}", activatedCount, topWaitingTokens.size());

        } catch (Exception e) {
            log.error("대기열 활성화 스케줄러 오류: {}", e.getMessage(), e);
        }

        log.info("=== 대기열 활성화 스케줄러 종료 ===");
    }

    /**
     * 만료된 대기열 토큰 정리 스케줄러
     * 1시간마다 실행
     *
     * 참고: Redis의 Sorted Set은 자동으로 만료되지 않으므로
     * 오래된 토큰을 주기적으로 정리해야 함
     */
    @Scheduled(fixedDelay = 3600000) // 1시간 (3600000ms)
    public void cleanupExpiredTokens() {
        log.info("=== 만료된 대기열 토큰 정리 스케줄러 시작 ===");

        try {
            // 현재 시각 - 대기 토큰 유효시간 = 삭제 기준 시각
            long currentTime = System.currentTimeMillis();
            long expirationTime = currentTime - (3600000); // 1시간 전

            // TODO: 실제 운영 시에는 모든 콘서트에 대해 수행
            String waitingKey = "waiting:queue:" + TEST_CONCERT_ID;

            // expirationTime 이전의 모든 토큰 삭제
            // Redis의 ZREMRANGEBYSCORE 명령 사용
            // redisTemplate.opsForZSet().removeRangeByScore(waitingKey, 0, expirationTime);

            log.info("만료된 대기열 토큰 정리 완료");

        } catch (Exception e) {
            log.error("만료된 토큰 정리 스케줄러 오류: {}", e.getMessage(), e);
        }

        log.info("=== 만료된 대기열 토큰 정리 스케줄러 종료 ===");
    }
}