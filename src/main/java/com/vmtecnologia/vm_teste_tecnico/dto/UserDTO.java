package com.vmtecnologia.vm_teste_tecnico.dto;

import com.vmtecnologia.vm_teste_tecnico.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "DTO para representação de usuário")
public class UserDTO {

    @Schema(description = "ID único do usuário", example = "1")
    private Long id;

    @Schema(description = "Nome completo do usuário", example = "Vinícius Exemplo")
    private String name;

    @Schema(description = "E-mail do usuário", example = "vinicius.exemplo@exemplo.com")
    private String email;

    @Schema(description = "Data de criação do registro", example = "2024-03-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Perfil de acesso do usuário", allowableValues = {"USER", "ADMIN"})
    private String role;

}