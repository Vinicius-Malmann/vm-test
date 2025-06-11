package com.vmtecnologia.vm_teste_tecnico.dto;

import com.vmtecnologia.vm_teste_tecnico.model.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {

    private Long id;

    private String nome;

    private String email;

    public UserDTO(User user) {
        this.id = user.getId();
        this.nome = user.getNome();
        this.email = user.getEmail();
    }
}
