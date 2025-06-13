package com.vmtecnologia.vm_teste_tecnico.controller;

import com.vmtecnologia.vm_teste_tecnico.dto.JwtResponse;
import com.vmtecnologia.vm_teste_tecnico.dto.LoginRequest;
import com.vmtecnologia.vm_teste_tecnico.service.AuthService;
import com.vmtecnologia.vm_teste_tecnico.service.TokenBlacklistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/vmtech/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para autenticação de usuários")
public class AuthController {

    private final AuthService authService;
    private final TokenBlacklistService tokenBlacklistService;

    @Operation(
            summary = "Autenticar usuário",
            description = "Autentica um usuário com credenciais válidas e retorna um token JWT",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Autenticação bem-sucedida",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = JwtResponse.class),
                                    examples = @ExampleObject(
                                            value = "{\"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Dados de entrada inválidos",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\"timestamp\": \"2023-11-20T12:00:00\", \"status\": 400, \"error\": \"Bad Request\", \"message\": \"Senha deve ter entre 8 e 30 caracteres\"}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Credenciais inválidas",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\"timestamp\": \"2023-11-20T12:05:00\", \"status\": 401, \"error\": \"Unauthorized\", \"message\": \"Credenciais inválidas\"}"
                                    )
                            )
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credenciais do usuário",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Login válido",
                                            value = "{\"username\": \"usuario123\", \"password\": \"Senha@123\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Login inválido",
                                            value = "{\"username\": \"user\", \"password\": \"123\"}"
                                    )
                            }
                    )
            )
            @Valid @RequestBody LoginRequest loginRequest) {

        log.info("Tentativa de login para usuário: {}", loginRequest.getUsername());

        JwtResponse response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(response);
    }


    @Operation(
            summary = "Deslogar usuário",
            description = "Invalida o token JWT atual",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Logout realizado com sucesso",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\"message\": \"Logout realizado com sucesso\"}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Não autorizado"
                    )
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); // Remove "Bearer "
        tokenBlacklistService.blacklistToken(token);

        log.info("Logout realizado para o token: {}", token);
        return ResponseEntity.ok().build();
    }
}
