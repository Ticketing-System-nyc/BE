package com.example.ticketing_system_project.domain.auth.dto;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor  // 기본 생성자 생성
@AllArgsConstructor // 전체 인자 생성자 생성
public class SignUpRequestDto {
    private String email;
    private String password;
    private String name;
}
