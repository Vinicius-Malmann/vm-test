package com.vmtecnologia.vm_teste_tecnico.service;

import com.vmtecnologia.vm_teste_tecnico.dto.CreateUserDTO;
import com.vmtecnologia.vm_teste_tecnico.dto.UpdateUserDTO;
import com.vmtecnologia.vm_teste_tecnico.dto.UserDTO;
import com.vmtecnologia.vm_teste_tecnico.exception.BusinessException;
import com.vmtecnologia.vm_teste_tecnico.exception.EmailSendingException;
import com.vmtecnologia.vm_teste_tecnico.model.User;
import com.vmtecnologia.vm_teste_tecnico.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    // Método auxiliar para criar User de teste
    private User createTestUser(Long id, String name, String username, String email, String password, String role) {
        return User.builder()
                .id(id)
                .name(name)
                .username(username)
                .email(email)
                .password(password)
                .role(role)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // Método auxiliar para criar CreateUserDTO de teste
    private CreateUserDTO createTestUserDTO(String name, String username, String email, String password, String role) {
        return CreateUserDTO.builder()
                .name(name)
                .username(username)
                .email(email)
                .password(password)
                .role(role)
                .build();
    }

    // Método auxiliar para criar UpdateUserDTO de teste
    private UpdateUserDTO createTestUpdateDTO(String name, String email, String password, String role) {
        return UpdateUserDTO.builder()
                .name(name)
                .email(email)
                .password(password)
                .role(role)
                .build();
    }

    @Test
    void createUser_ShouldReturnUserDTO_WhenValidInput() {
        // Arrange
        CreateUserDTO dto = createTestUserDTO("John Doe", "john.doe", "john@email.com", "Senha@123", "USER");
        User savedUser = createTestUser(1L, "John Doe", "john.doe", "john@email.com", "encodedPassword", "USER");

        when(userRepository.existsByEmail("john@email.com")).thenReturn(false);
        when(passwordEncoder.encode("Senha@123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserDTO result = userService.createUser(dto);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john@email.com", result.getEmail());
        verify(emailService).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void createUser_ShouldThrow_WhenEmailAlreadyExists() {
        // Arrange
        CreateUserDTO dto = createTestUserDTO("John Doe", "john.doe", "john@email.com", "Senha@123", "USER");
        when(userRepository.existsByEmail("john@email.com")).thenReturn(true);

        // Act & Assert
        assertThrows(BusinessException.class, () -> userService.createUser(dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_ShouldRollback_WhenEmailSendingFails() {
        // Arrange
        CreateUserDTO dto = createTestUserDTO("John Doe", "john.doe", "john@email.com", "Senha@123", "USER");
        User user = createTestUser(1L, "John Doe", "john.doe", "john@email.com", "encodedPassword", "USER");

        when(userRepository.existsByEmail("john@email.com")).thenReturn(false);
        when(passwordEncoder.encode("Senha@123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        doThrow(new EmailSendingException("SMTP error")).when(emailService).sendEmail(anyString(), anyString(), anyString());

        // Act & Assert
        assertThrows(BusinessException.class, () -> userService.createUser(dto));
    }

    @Test
    void updateUser_ShouldUpdateUser_WhenValidInput() {
        // Arrange
        Long userId = 1L;
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setName("Novo Nome");
        dto.setEmail("novo@email.com");
        dto.setPassword("novaSenha");
        dto.setRole("ADMIN");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("Nome Antigo");
        existingUser.setEmail("antigo@email.com");
        existingUser.setPassword("senhaAntiga");
        existingUser.setRole("USER");

        // Configura os mocks
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("novo@email.com")).thenReturn(false);
        when(passwordEncoder.encode("novaSenha")).thenReturn("senhaCriptografada");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(userId); // Simula o salvamento
            return savedUser;
        });

        // Act
        UserDTO result = userService.updateUser(userId, dto);

        // Assert
        assertNotNull(result);
        assertEquals("Novo Nome", result.getName());
        assertEquals("novo@email.com", result.getEmail());
        assertEquals("ADMIN", result.getRole());

        // Verifica se os métodos foram chamados
        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmail("novo@email.com");
        verify(passwordEncoder).encode("novaSenha");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_ShouldThrow_WhenUserNotFound() {
        // Arrange
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> userService.updateUser(userId, createTestUpdateDTO(null, null, null, null)));
    }

    @Test
    void updateUser_ShouldThrow_WhenNewEmailAlreadyExists() {
        // Arrange
        Long userId = 1L;
        UpdateUserDTO dto = createTestUpdateDTO("John", "existing@email.com", null, null);
        User existingUser = createTestUser(userId, "John", "john.doe", "john@email.com", "encoded", "USER");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("existing@email.com")).thenReturn(true);

        // Act & Assert
        assertThrows(BusinessException.class,
                () -> userService.updateUser(userId, dto));
    }

    @Test
    void usersList_ShouldReturnFilteredUsers_WhenNameFilterProvided() {
        // Arrange
        String filter = "John";
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = List.of(
                createTestUser(1L, "John Doe", "john.doe", "john@email.com", "encoded", "USER")
        );
        Page<User> page = new PageImpl<>(users);

        when(userRepository.findByNameContainingIgnoreCase(filter, pageable)).thenReturn(page);

        // Act
        Page<UserDTO> result = userService.usersList(filter, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("John Doe", result.getContent().get(0).getName());
    }

    @Test
    void usersList_ShouldReturnAllUsers_WhenNoFilter() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = List.of(
                createTestUser(1L, "John Doe", "john.doe", "john@email.com", "encoded", "USER"),
                createTestUser(2L, "Jane Doe", "jane.doe", "jane@email.com", "encoded", "USER")
        );
        Page<User> page = new PageImpl<>(users);

        when(userRepository.findAll(pageable)).thenReturn(page);

        // Act
        Page<UserDTO> result = userService.usersList(null, pageable);

        // Assert
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void findById_ShouldReturnUser_WhenExists() {
        // Arrange
        Long userId = 1L;
        User user = createTestUser(userId, "John Doe", "john.doe", "john@email.com", "encoded", "USER");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        UserDTO result = userService.findById(userId);

        // Assert
        assertEquals("John Doe", result.getName());
        assertEquals("john@email.com", result.getEmail());
    }

    @Test
    void findById_ShouldThrow_WhenUserNotFound() {
        // Arrange
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> userService.findById(userId));
    }

    @Test
    void deleteUser_ShouldDelete_WhenUserExists() {
        // Arrange
        Long userId = 1L;
        User user = createTestUser(userId, "John Doe", "john.doe", "john@email.com", "encoded", "USER");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository).delete(user);
        verify(emailService).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void deleteUser_ShouldThrow_WhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> userService.deleteUser(99L));
    }

    @Test
    void validateEmailNotInUse_ShouldThrow_WhenEmailExists() {
        when(userRepository.existsByEmail("existing@email.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> userService.validateEmailNotInUse("existing@email.com"));
    }

    @Test
    void convertToDTO_ShouldConvertCorrectly() {
        // Arrange
        User user = createTestUser(1L, "John", "john.doe", "john@email.com", "encoded", "ADMIN");
        user.setCreatedAt(LocalDateTime.now());

        // Act
        UserDTO dto = userService.convertToDTO(user);

        // Assert
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
        assertEquals(user.getRole(), dto.getRole());
        assertEquals(user.getCreatedAt(), dto.getCreatedAt());
    }
}