//package com.gullak.wallet.service;
//
//import io.smallrye.jwt.build.Jwt;
//import jakarta.enterprise.context.ApplicationScoped;
//
//@ApplicationScoped
//public class JwtService {
//
//    public String generateToken(String username){
//        return Jwt.issuer("gullak-wallet")
//                .subject(username)
//                .expiresIn(3600)
//                .sign();
//
//    }
//
//
//}


package com.gullak.wallet.security;

import jakarta.enterprise.context.ApplicationScoped;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@ApplicationScoped
public class JwtService {

    private static final String SECRET = System.getenv("JWT_SECRET");
    private static final long EXPIRATION = 3600;

    private static final Base64.Encoder ENCODER =
            Base64.getUrlEncoder().withoutPadding();

    public String generateToken(String username) {

        try {
            long now = System.currentTimeMillis() / 1000;

            String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";

            String payload = "{\"iss\":\"gullak-wallet\","
                    + "\"sub\":\"" + username + "\","
                    + "\"iat\":" + now + ","
                    + "\"exp\":" + (now + EXPIRATION) + "}";

            String encodedHeader = encode(header);
            String encodedPayload = encode(payload);

            String data = encodedHeader + "." + encodedPayload;

            String signature = sign(data);

            return data + "." + signature;

        } catch (Exception e) {
            throw new RuntimeException("Token generation failed", e);
        }
    }

    private String encode(String value) {
        return ENCODER.encodeToString(
                value.getBytes(StandardCharsets.UTF_8)
        );
    }

    private String sign(String data) throws Exception {

        if (SECRET == null || SECRET.length() < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET must be at least 32 characters"
            );
        }

        Mac mac = Mac.getInstance("HmacSHA256");

        SecretKeySpec key = new SecretKeySpec(
                SECRET.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        mac.init(key);

        byte[] signature = mac.doFinal(
                data.getBytes(StandardCharsets.UTF_8)
        );

        return ENCODER.encodeToString(signature);
    }
}