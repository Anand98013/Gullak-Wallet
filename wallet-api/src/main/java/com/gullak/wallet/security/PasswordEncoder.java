package com.gullak.wallet.security;

import jakarta.enterprise.context.ApplicationScoped;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@ApplicationScoped
public class PasswordEncoder {

    public String hashPassword(String password) throws Exception {

        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        sha1.update(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        sha1.update(salt);

        byte[] hash = sha1.digest();

        byte[] combined = new byte[hash.length + salt.length];

        System.arraycopy(hash, 0, combined, 0, hash.length);
        System.arraycopy(salt, 0, combined, hash.length, salt.length);

        return "{SSHA}" + Base64.getEncoder().encodeToString(combined);
    }
}