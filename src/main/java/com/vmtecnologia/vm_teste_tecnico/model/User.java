package com.vmtecnologia.vm_teste_tecnico.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Entidade que representa um usuário do sistema")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único do usuário", example = "1")
    private Long id;

    @Column(nullable = false, length = 100)
    @Schema(description = "Nome completo do usuário", example = "Vinícius Exemplo", required = true)
    private String nome;

    @Column(nullable = false, unique = true, length = 100)
    @Schema(description = "E-mail do usuário", example = "vincius.exemplo@exemplo.com", required = true)
    private String email;

    @Column(nullable = false, length = 200)
    @Schema(description = "Senha criptografada do usuário", required = true)
    private String senha;

    @CreationTimestamp
    @Column(name = "data_criacao", updatable = false)
    @Schema(description = "Data e hora de criação do registro", example = "2024-03-15T10:30:00")
    private LocalDateTime dataCriacao;

}