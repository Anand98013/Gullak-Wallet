package com.gullak.wallet.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthenticatedUser {

    private String userId;
    private String username;
    private String role;
}