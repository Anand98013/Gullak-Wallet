package com.gullak.wallet.security;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JwtService {

//    public String generateToken(String userId, String username){
//        return Jwt.issuer("gullak-wallet")
//                .subject(userId)
//                .claim("userId", userId)
//                .claim("username", username)
//                .expiresIn(3600)
//                .sign();
//
//    }

    public String generateToken(String userId, String username, String role) {
        return Jwt.issuer("gullak-wallet")
                .subject(userId)
                .claim("userId", userId)
                .claim("username", username)
                .groups(java.util.Set.of(role))
                .expiresIn(3600)
                .sign();
    }

}