package com.example.ticketing_system_project.domain.auth.controller;

import com.example.ticketing_system_project.domain.auth.dto.SignUpRequestDto;
import com.example.ticketing_system_project.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/signup")
    public String signup(@RequestBody SignUpRequestDto dto) {
// 이 로그가 콘솔에 찍히는지 반드시 확인해야 합니다.
        System.out.println("========= 요청 들어옴 =========");
        System.out.println("Email: " + dto.getEmail());
        return userService.signup(dto);
    }

    @PostMapping("/login")
    public String login(@RequestBody SignUpRequestDto dto) {
        return userService.login(dto);
    }
}
