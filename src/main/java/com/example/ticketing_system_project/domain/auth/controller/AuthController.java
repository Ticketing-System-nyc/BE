package com.example.ticketing_system_project.domain.auth.controller;

import com.example.ticketing_system_project.domain.auth.dto.LoginRequestDto;
import com.example.ticketing_system_project.domain.auth.dto.SignUpRequestDto;
import com.example.ticketing_system_project.domain.auth.dto.TokenResponseDto;
import com.example.ticketing_system_project.domain.user.service.UserService;
import jakarta.validation.Valid;
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
    public String signup(@Valid @RequestBody SignUpRequestDto dto) {
        return userService.signup(dto);
    }

    @PostMapping("/login")
    public TokenResponseDto login(@Valid @RequestBody LoginRequestDto dto) {
        return userService.login(dto);
    }
}
