package com.vmtecnologia.vm_teste_tecnico.service;

import com.vmtecnologia.vm_teste_tecnico.dto.CreateUserDTO;
import com.vmtecnologia.vm_teste_tecnico.dto.UpdateUserDTO;
import com.vmtecnologia.vm_teste_tecnico.dto.UserDTO;
import com.vmtecnologia.vm_teste_tecnico.model.User;
import com.vmtecnologia.vm_teste_tecnico.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import org.springframework.util.StringUtils;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@Tag(name = "Serviço de Usuários", description = "Contém as operações de negócio relacionadas a usuários")
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    public UserDTO createUser(CreateUserDTO userDTO) {
        validateEmailNotInUse(userDTO.getEmail());

        User user = userDTO.toEntity(passwordEncoder);
        User savedUser = userRepository.save(user);

        sendWelcomeEmail(savedUser);

        return convertToDTO(savedUser);
    }

    public UserDTO updateUser(Long id, UpdateUserDTO updateUserDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado para o id: " + id));

        // Verifica se o novo email (se diferente) não está em uso
        if (!user.getEmail().equals(updateUserDTO.getEmail())) {
            validateEmailNotInUse(updateUserDTO.getEmail());
        }

        // Atualiza os campos
        user.setName(updateUserDTO.getName());
        user.setEmail(updateUserDTO.getEmail());

        if (updateUserDTO.getPassword() != null && !updateUserDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateUserDTO.getPassword()));
        }

        if (updateUserDTO.getRole() != null) {
            user.setRole(updateUserDTO.getRole());
        }

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }


    public Page<UserDTO> usersList(String nameFilter, Pageable pageable) {
        if (StringUtils.hasText(nameFilter)) {
            return userRepository.findByNameContainingIgnoreCase(nameFilter, pageable)
                    .map(this::convertToDTO);
        }
        return userRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    public UserDTO findById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado para o id: " + id));
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado para o id: " + id));

        userRepository.delete(user);
        sendAccountDeletionEmail(user);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setRole(user.getRole());
        return dto;
    }


    private void validateEmailNotInUse(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email já cadastrado: " + email);
        }
    }

    private void sendWelcomeEmail(User user) {
        try {
            emailService.sendEmail(
                    user.getEmail(),
                    "Cadastro efetuado com sucesso!",
                    "Olá, " + user.getName() + ",\n\nSua conta foi cadastrada com sucesso!"
            );
        } catch (Exception e) {
            log.error("Erro ao enviar email para {}", user.getEmail(), e);
        }
    }

    private void sendAccountDeletionEmail(User user) {
        try {
            emailService.sendEmail(
                    user.getEmail(),
                    "Sua conta foi deletado com sucesso!",
                    "Olá " + user.getName() + ",\n\nSua conta foi deletada com sucesso!."
            );
        } catch (Exception e) {
            log.error("Erro ao enviar email para {}", user.getEmail(), e);
        }
    }
}