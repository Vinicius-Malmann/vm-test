package com.vmtecnologia.vm_teste_tecnico.service;

import com.vmtecnologia.vm_teste_tecnico.config.JwtConfig;
import com.vmtecnologia.vm_teste_tecnico.dto.JwtResponse;
import com.vmtecnologia.vm_teste_tecnico.dto.LoginRequest;
import com.vmtecnologia.vm_teste_tecnico.model.User;
import com.vmtecnologia.vm_teste_tecnico.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;
    private final UserRepository userRepository;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            username,
                            loginRequest.getPassword()
                    )
            );

            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> {
                        log.error("Usuário autenticado não encontrado: {}", username);
                        return new UsernameNotFoundException("Usuário não encontrado");
                    });

            log.info("Login bem-sucedido para usuário: {}", username);
            return buildJwtResponse(user);

        } catch (BadCredentialsException e) {
            log.warn("Credenciais inválidas para usuário: {}", username);
            throw new AuthenticationServiceException("Credenciais inválidas", e);
        }
    }

    private JwtResponse buildJwtResponse(User user) {
        String token = generateToken(user);
        Instant expiration = Instant.now().plusMillis(jwtConfig.getExpirationTime());

        return JwtResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtConfig.getExpirationTime() / 1000) // em segundos
                .expiresAt(expiration)
                .username(user.getUsername())
                .build();
    }

    private String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(jwtConfig.getExpirationTime());

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }
}