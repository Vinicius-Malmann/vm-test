package com.vmtecnologia.vm_teste_tecnico.config;

import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String principal;
    private final String credentials;
    private final Claims claims;

    public JwtAuthenticationToken(Claims claims) {
        super(extractAuthorities(claims));
        this.principal = claims.getSubject();
        this.credentials = null;
        this.claims = claims;
        super.setAuthenticated(true);
    }

    private static Collection<? extends GrantedAuthority> extractAuthorities(Claims claims) {
        // Extrai roles do token JWT (ajuste conforme sua implementação)
        List<String> roles = claims.get("roles", List.class);
        if (roles == null) {
            return Collections.emptyList();
        }
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public Claims getClaims() {
        return claims;
    }
}
