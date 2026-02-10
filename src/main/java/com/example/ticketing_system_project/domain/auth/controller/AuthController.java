package com.example.ticketing_system_project.domain.auth.controller;

import com.example.ticketing_system_project.domain.auth.dto.LoginRequestDto;
import com.example.ticketing_system_project.domain.auth.dto.SignUpRequestDto;
import com.example.ticketing_system_project.domain.auth.dto.SignUpResponseDto;
import com.example.ticketing_system_project.domain.auth.dto.TokenResponseDto;
import com.example.ticketing_system_project.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<SignUpResponseDto> signup(@Valid @RequestBody SignUpRequestDto dto) { // 반환 타입 변경
        // return userService.signup(dto);
        SignUpResponseDto response = userService.signup(dto);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto dto) { // ResponseEntity 추가
        // return userService.login(dto);
        TokenResponseDto response = userService.login(dto);
        return ResponseEntity.ok(response);
    }
}
