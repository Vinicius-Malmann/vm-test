package com.vmtecnologia.vm_teste_tecnico.dto;

import com.vmtecnologia.vm_teste_tecnico.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CreateUserDTO {
    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    // Adicione esses campos que estavam faltando
    @NotBlank
    private String username;

    private String role = "USER"; // Valor padrão

    public User toEntity(PasswordEncoder passwordEncoder) {
        return User.builder()
                .name(this.name)
                .email(this.email)
                .username(this.username)
                .password(passwordEncoder.encode(this.password))
                .role(this.role)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
