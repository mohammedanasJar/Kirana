package com.example.Kirana.models;

import lombok.Data;

@Data
public class AuthenticationResponse {
    private String token;
    private String refreshToken;
}
