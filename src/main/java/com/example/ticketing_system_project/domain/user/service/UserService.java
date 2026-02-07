package com.example.ticketing_system_project.domain.user.service;

import com.example.ticketing_system_project.domain.auth.dto.SignUpRequestDto;
import com.example.ticketing_system_project.domain.user.entity.User;
import com.example.ticketing_system_project.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public String signup(SignUpRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "이미 존재하는 이메일입니다.";
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // 암호화 없이 저장
        user.setName(request.getName());
        userRepository.save(user);
        return "회원가입 성공";
    }

    public String login(SignUpRequestDto request) {
        return userRepository.findByEmail(request.getEmail())
                .filter(u -> u.getPassword().equals(request.getPassword()))
                .map(u -> "로그인 성공! 환영합니다 " + u.getName() + "님")
                .orElse("이메일 또는 비밀번호가 틀렸습니다.");
    }
}
