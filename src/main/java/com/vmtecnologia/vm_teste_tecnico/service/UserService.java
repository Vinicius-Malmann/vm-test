package com.vmtecnologia.vm_teste_tecnico.service;

import com.vmtecnologia.vm_teste_tecnico.dto.CreateUserDTO;
import com.vmtecnologia.vm_teste_tecnico.dto.UpdateUserDTO;
import com.vmtecnologia.vm_teste_tecnico.dto.UserDTO;
import com.vmtecnologia.vm_teste_tecnico.exception.BusinessException;
import com.vmtecnologia.vm_teste_tecnico.exception.EmailSendingException;
import com.vmtecnologia.vm_teste_tecnico.model.User;
import com.vmtecnologia.vm_teste_tecnico.repository.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
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
        try {
            validateEmailNotInUse(userDTO.getEmail());

            User user = userDTO.toEntity(passwordEncoder);
            User savedUser = userRepository.save(user);

            sendWelcomeEmail(savedUser); // Pode lançar EmailSendingException

            return convertToDTO(savedUser);

        } catch (EmailSendingException e) {
            log.error("Falha no envio de email para {}", userDTO.getEmail(), e);
            throw new BusinessException("Cadastro realizado, mas não foi possível enviar o email de confirmação");

        } catch (DataIntegrityViolationException e) {
            log.error("Violação de integridade ao criar usuário", e);
            throw new BusinessException("Email já está em uso");

        } catch (Exception e) {
            log.error("Erro inesperado ao criar usuário", e);
            throw new BusinessException("Erro ao processar seu cadastro");
        }
    }

    @Transactional
    public UserDTO updateUser(Long id, UpdateUserDTO updateUserDTO) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado para o id: " + id));

            if (!user.getEmail().equals(updateUserDTO.getEmail())) {
                validateEmailNotInUse(updateUserDTO.getEmail());
            }

            user.setName(updateUserDTO.getName());
            user.setEmail(updateUserDTO.getEmail());

            if (updateUserDTO.getPassword() != null && !updateUserDTO.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updateUserDTO.getPassword()));
            }

            if (updateUserDTO.getRole() != null) {
                user.setRole(updateUserDTO.getRole());
            }

            User updatedUser = userRepository.save(user);
            sendUpdateEmail(updatedUser);
            return convertToDTO(updatedUser);

        } catch (EmailSendingException e) {
            log.error("Falha no envio de email de atualização", e);
            throw new BusinessException("Dados atualizados, mas não foi possível enviar o email de confirmação");
        } catch (Exception e) {
            log.error("Erro inesperado ao atualizar usuário", e);
            throw new BusinessException("Erro ao atualizar seus dados");
        }
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

    @Transactional
    public void deleteUser(Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado para o id: " + id));

            userRepository.delete(user);
            sendAccountDeletionEmail(user);

        } catch (EmailSendingException e) {
            log.error("Falha no envio de email de exclusão", e);
            throw new BusinessException("Conta excluída, mas não foi possível enviar a confirmação");
        } catch (Exception e) {
            log.error("Erro inesperado ao excluir usuário", e);
            throw new BusinessException("Erro ao excluir sua conta");
        }
    }

    UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setRole(user.getRole());
        return dto;
    }


    void validateEmailNotInUse(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email já cadastrado: " + email);
        }
    }

    private void sendWelcomeEmail(User user) {
        try {
            emailService.sendEmail(
                    user.getEmail(),
                    "Bem-vindo ao nosso sistema!",
                    "Olá " + user.getName() + ", seu cadastro foi realizado com sucesso!"
            );
        } catch (Exception e) {
            throw new EmailSendingException("Falha ao enviar email de boas-vindas", e);
        }
    }

    private void sendUpdateEmail(User user) {
        try {
            emailService.sendEmail(
                    user.getEmail(),
                    "Seus dados foram atualizados!",
                    "Olá, " + user.getName() + ",\n\n" +
                            "Informamos que os dados da sua conta foram atualizados com sucesso!"
            );
        } catch (Exception e) {
            log.error("Erro ao enviar email de atualização para {}", user.getEmail(), e);
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