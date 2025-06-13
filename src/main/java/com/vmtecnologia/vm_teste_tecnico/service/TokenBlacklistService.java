package com.vmtecnologia.vm_teste_tecnico.service;

import com.vmtecnologia.vm_teste_tecnico.config.JwtConfig;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class TokenBlacklistService {

    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();
    private final SecretKey secretKey;

    public TokenBlacklistService(JwtConfig jwtConfig) {
        this.secretKey = jwtConfig.getSecretKey();
    }

    public void blacklistToken(String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            // Verifica se o token já expirou
            if (jws.getPayload().getExpiration().after(new Date())) {
                blacklistedTokens.add(token);
                log.debug("Token adicionado à blacklist: {}", token);
            }
        } catch (ExpiredJwtException e) {
            log.warn("Token expirado recebido para blacklist: {}", token);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token inválido recebido para blacklist: {} - Erro: {}", token, e.getMessage());
        }
    }

    public boolean isTokenBlacklisted(String token) {
        return token == null || token.isEmpty() || blacklistedTokens.contains(token);
    }

    @Scheduled(fixedRate = 3600000) // Limpeza a cada hora
    public void cleanupExpiredTokens() {
        log.info("Iniciando limpeza de tokens expirados na blacklist...");
        int initialSize = blacklistedTokens.size();

        blacklistedTokens.removeIf(token -> {
            try {
                Claims claims = Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                return claims.getExpiration().before(new Date());
            } catch (JwtException | IllegalArgumentException e) {
                return true; // Remove tokens inválidos
            }
        });

        log.info("Limpeza concluída. Tokens removidos: {}", initialSize - blacklistedTokens.size());
    }
}