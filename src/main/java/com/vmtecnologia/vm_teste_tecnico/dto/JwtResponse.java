package com.vmtecnologia.vm_teste_tecnico.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {

    private String token;

    private String tokenType;

    private Instant expiresAt;

    private Long expiresIn;

    private String username;

}
