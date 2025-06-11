package com.vmtecnologia.vm_teste_tecnico.service;

import com.vmtecnologia.vm_teste_tecnico.dto.UserDTO;
import com.vmtecnologia.vm_teste_tecnico.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@Tag(name = "Serviço de Usuários", description = "Contém as operações de negócio relacionadas a usuários")
public class UserService {

    private final UserRepository UserRepository;
    private final EmailService emailService;

    public User criarUser(createUser dto) {
        // Verifica se email já existe
        if (UserRepository.existsByEmail(dto.getEmail())) {
            throw new EmailJaCadastradoException(dto.getEmail());
        }

        // Cria o usuário
        User User = new User(dto.getNome(), dto.getEmail(), passwordEncoder.encode(dto.getSenha()));
        User = UserRepository.save(User);

        try {
            // Envia email (depois de salvar para garantir que o usuário existe)
            emailService.enviarEmail(
                    User.getEmail(),
                    "Cadastro realizado com sucesso",
                    "Olá " + User.getNome() + ", seu cadastro foi realizado com sucesso!"
            );
        } catch (Exception e) {
            log.error("Falha ao enviar email para o usuário " + User.getEmail(), e);
            // Não lança exceção para não reverter a transação
        }

        return User;
    }

    public Page<UserDTO> listarUsers(String filtroNome, Pageable pageable) {
        Page<User> Users;
        if (filtroNome != null && !filtroNome.isEmpty()) {
            Users = UserRepository.findByNomeContainingIgnoreCase(filtroNome, pageable);
        } else {
            Users = UserRepository.findAll(pageable);
        }
        return Users.map(UserDTO::new);
    }

    public UserDTO buscarPorId(Long id) {
        User User = UserRepository.findById(id)
                .orElseThrow(() -> new UserNaoEncontradoException(id));
        return new UserDTO(User);
    }

}