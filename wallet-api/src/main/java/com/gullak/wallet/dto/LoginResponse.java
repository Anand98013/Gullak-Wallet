package com.gullak.wallet.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {

    private String message;
    private String username;
    private String token;
    private String tokenType;
    private long expiresIn;
}