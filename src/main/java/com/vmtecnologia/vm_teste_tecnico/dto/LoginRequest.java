package com.vmtecnologia.vm_teste_tecnico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Username não pode estar vazio")
    @Size(min = 4, max = 20, message = "Username deve ter entre 4 e 20 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username contém caracteres inválidos")
    private String username;

    @NotBlank(message = "Senha não pode estar vazia")
    @Size(min = 8, max = 30, message = "Senha deve ter entre 8 e 30 caracteres")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "Senha deve conter: 1 letra maiúscula, 1 minúscula, 1 número e 1 caractere especial"
    )
    private String password;

}
