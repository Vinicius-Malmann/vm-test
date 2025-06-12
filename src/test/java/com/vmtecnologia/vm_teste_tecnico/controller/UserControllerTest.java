package com.vmtecnologia.vm_teste_tecnico.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmtecnologia.vm_teste_tecnico.config.SecurityConfig;
import com.vmtecnologia.vm_teste_tecnico.dto.CreateUserDTO;
import com.vmtecnologia.vm_teste_tecnico.dto.UserDTO;
import com.vmtecnologia.vm_teste_tecnico.service.AuthService;
import com.vmtecnologia.vm_teste_tecnico.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@Import({SecurityConfig.class, UserService.class})
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private AuthService authService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_ShouldReturnCreated_WhenValidInput() throws Exception {
        CreateUserDTO request = CreateUserDTO.builder()
                .name("Test User")
                .email("test@email.com")
                .password("Test@123")
                .username("test.user")
                .build();

        UserDTO response = UserDTO.builder()
                .id(1L)
                .name("Test User")
                .email("test@email.com")
                .username("test.user")
                .role("USER")
                .build();

        when(userService.createUser(any(CreateUserDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void createUser_ShouldReturnBadRequest_WhenInvalidEmail() throws Exception {
        CreateUserDTO invalidRequest = CreateUserDTO.builder()
                .name("Test User")
                .email("email-invalido")
                .password("Test@123")
                .username("test.user")
                .build();

        mockMvc.perform(post("/vmtech/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("email"));
    }

    @Test
    void getUserById_ShouldReturnUser_WhenExists() throws Exception {
        UserDTO user = UserDTO.builder()
                .id(1L)
                .name("Existing User")
                .email("existing@email.com")
                .build();

        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(get("/vmtech/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Existing User"));
    }

    @Test
    void getUserById_ShouldReturnNotFound_WhenNotExists() throws Exception {
        when(userService.findById(99L)).thenThrow(new EntityNotFoundException("User not found"));

        mockMvc.perform(get("/vmtech/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }
}