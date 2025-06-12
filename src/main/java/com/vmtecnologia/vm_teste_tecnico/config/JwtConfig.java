package com.vmtecnologia.vm_teste_tecnico.config;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationTime;

    @PostConstruct
    public void validate() {
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException("JWT secret deve ter pelo menos 32 caracteres");
        }
    }

    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public long getExpirationTime() {
        return expirationTime;
    }
}

