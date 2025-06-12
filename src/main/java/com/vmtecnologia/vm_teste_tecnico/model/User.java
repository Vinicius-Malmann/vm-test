package com.vmtecnologia.vm_teste_tecnico.model;

import io.micrometer.common.util.internal.logging.InternalLogger;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Entidade que representa um usuário do sistema")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único do usuário", example = "1")
    private Long id;

    @Column(nullable = false, length = 100)
    @Schema(description = "Nome completo do usuário", example = "Vinícius Exemplo", required = true)
    private String name;

    @Column(length = 255, unique = true, nullable = false)
    @Schema(description = "Nome de usuário para login", example = "viniciuas.teste", requiredMode = Schema.RequiredMode.REQUIRED, pattern = "^[a-zA-Z0-9._-]+$")
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    @Schema(description = "E-mail do usuário", example = "vincius.exemplo@exemplo.com", required = true)
    private String email;

    @Column(nullable = false, length = 200)
    @Schema(description = "Senha criptografada do usuário", required = true)
    private String password;

    @CreationTimestamp
    @Column(name = "data_criacao", updatable = false)
    @Schema(description = "Data e hora de criação do registro", example = "2024-03-15T10:30:00")
    private LocalDateTime createdAt;

    @Column(length = 255)
    @Schema(description = "Perfil de acesso do usuário",
            allowableValues = {"USER", "ADMIN"})
    private String role;


}