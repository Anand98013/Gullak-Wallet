package com.gullak.wallet.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class AuthenticatedUser {

    private String userId;
    private String username;
    private String role;
}