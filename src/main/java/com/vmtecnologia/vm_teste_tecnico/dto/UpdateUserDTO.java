package com.vmtecnologia.vm_teste_tecnico.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserDTO {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @Size(min = 6)
    private String password;

    private String role;
}