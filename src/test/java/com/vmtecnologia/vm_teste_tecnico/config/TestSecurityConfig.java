package com.vmtecnologia.vm_teste_tecnico.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import java.util.Collections;

@Configuration
@Profile("test")
public class TestSecurityConfig {

    @Bean
    public JwtConfig testJwtConfig() {
        JwtConfig jwtConfig = new JwtConfig();
        jwtConfig.setSecret("Ab1!Xy9@Lu3#Pq7^ZrLm*Go2$TxQCsaeaefdf");
        jwtConfig.setExpirationTime(86400000L);
        return jwtConfig;
    }

    @Bean
    public SecretKey secretKey(JwtConfig jwtConfig) {
        return jwtConfig.getSecretKey();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> new org.springframework.security.core.userdetails.User(
                "testuser",
                passwordEncoder().encode("password"),
                Collections.singletonList(new SimpleGrantedAuthority("USER"))
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll());
        return http.build();
    }
}