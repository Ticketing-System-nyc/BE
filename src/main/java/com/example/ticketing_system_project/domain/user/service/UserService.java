package com.example.ticketing_system_project.domain.user.service;

import com.example.ticketing_system_project.domain.auth.dto.LoginRequestDto;
import com.example.ticketing_system_project.domain.auth.dto.LoginResponseDto;
import com.example.ticketing_system_project.domain.auth.dto.SignUpRequestDto;
import com.example.ticketing_system_project.domain.auth.dto.TokenResponseDto;
import com.example.ticketing_system_project.domain.user.entity.User;
import com.example.ticketing_system_project.domain.user.repository.UserRepository;
import com.example.ticketing_system_project.global.exception.custom.InvalidPasswordException;
import com.example.ticketing_system_project.global.exception.custom.UserNotFoundException;
import com.example.ticketing_system_project.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    public String signup(SignUpRequestDto dto) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(dto.getEmail())) {
            return "이미 존재하는 이메일입니다.";
        }
        User user = new User();
        user.setEmail(dto.getEmail());
        // 암호화 적용: passwordEncoder.encode() 사용
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName());

        userRepository.save(user);
        return "회원가입 성공";
    }

    // 로그인 성공 시 TokenResponseDto(accessToken, refreshToken)를 반환
    @Transactional
    public TokenResponseDto login(LoginRequestDto dto) {
        // 1. 사용자 조회
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("이메일 또는 비밀번호가 올바르지 않습니다."));

        // 2. 비밀번호 검증 (암호화된 비밀번호와 매칭 확인)
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 3. JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        // 4. TokenResponseDto 반환
        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
