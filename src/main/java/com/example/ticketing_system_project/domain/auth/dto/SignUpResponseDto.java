package com.example.ticketing_system_project.domain.auth.dto;

import com.example.ticketing_system_project.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpResponseDto {
    private Long userId;
    private String email;
    private String name;

    public static SignUpResponseDto from(User user) {
        return SignUpResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}