package com.vmtecnologia.vm_teste_tecnico.service;

import com.vmtecnologia.vm_teste_tecnico.dto.CreateUserDTO;
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
        private final ModelMapper modelMapper;

        public UserDTO createUser(CreateUserDTO userDTO) {
            validateEmailNotInUse(userDTO.getEmail());

            User user = convertToEntity(userDTO);
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            User savedUser = userRepository.save(user);

            sendWelcomeEmail(savedUser);

            return convertToDTO(savedUser);
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
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        }

        public void deleteUser(Long id) {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

            if (hasDependencies(user)) {
                throw new DataIntegrityViolationException("Cannot delete user with existing references");
            }

            userRepository.delete(user);
            sendAccountDeletionEmail(user);
        }


        private void validateEmailNotInUse(String email) {
            if (userRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("Email already in use: " + email);
            }
        }

        private void sendWelcomeEmail(User user) {
            try {
                emailService.sendEmail(
                        user.getEmail(),
                        "Welcome to our platform!",
                        "Hello " + user.getName() + ",\n\nYour account has been successfully created!"
                );
            } catch (Exception e) {
                log.error("Failed to send welcome email to user {}", user.getEmail(), e);
            }
        }

        private void sendAccountDeletionEmail(User user) {
            try {
                emailService.sendEmail(
                        user.getEmail(),
                        "Your account has been deleted",
                        "Hello " + user.getName() + ",\n\nYour account has been successfully deleted."
                );
            } catch (Exception e) {
                log.error("Failed to send deletion email to user {}", user.getEmail(), e);
            }
        }
    }


}