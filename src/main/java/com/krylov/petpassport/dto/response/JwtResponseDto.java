package com.krylov.petpassport.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class JwtResponseDto {
    private String accessToken;
    private String type = "Bearer";
    private String refreshToken;
    private Long id;
    private String email;
    private List<String> roles;

    public JwtResponseDto(String accessToken, String refreshToken, Long id, String email, List<String> roles) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
        this.email = email;
        this.roles = roles;
    }
}
