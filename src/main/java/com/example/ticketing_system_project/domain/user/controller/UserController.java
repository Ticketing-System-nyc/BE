package com.example.ticketing_system_project.domain.user.controller;

import com.example.ticketing_system_project.domain.user.entity.User;
import com.example.ticketing_system_project.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    // 인증이 필요한 엔드포인트 (테스트용)
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName(); // JWT에서 추출한 userId

        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        UserInfoResponse response = new UserInfoResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPoint()
        );

        return ResponseEntity.ok(response);
    }

    // 내부 클래스로 응답 DTO 정의
    public record UserInfoResponse(Long userId, String email, String name, Long point) {}
}