package com.vmtecnologia.vm_teste_tecnico.controller;

import com.vmtecnologia.vm_teste_tecnico.dto.CreateUserDTO;
import com.vmtecnologia.vm_teste_tecnico.dto.UpdateUserDTO;
import com.vmtecnologia.vm_teste_tecnico.dto.UserDTO;
import com.vmtecnologia.vm_teste_tecnico.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/vmtech/users")
@Tag(name = "Users", description = "Operações relacionadas a usuários")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/createUser")
    @Operation(summary = "Criar novo usuário", description = "Registra um novo usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso", content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    })
    public ResponseEntity<UserDTO> createUser(
            @Valid @RequestBody @Parameter(description = "Dados do usuário a ser criado", required = true) CreateUserDTO UserDTO) {

        UserDTO newUser = userService.createUser(UserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Novo e-mail já está em uso")
    })
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "ID do usuário a ser atualizado", required = true)
            @PathVariable Long id,

            @Valid @RequestBody @Parameter(description = "Dados atualizados do usuário", required = true)
            UpdateUserDTO updateUserDTO) {

        UserDTO updatedUser = userService.updateUser(id, updateUserDTO);
        return ResponseEntity.ok(updatedUser);
    }


    @GetMapping
    @Operation(summary = "Listar usuários", description = "Retorna lista paginada de usuários com filtro opcional por nome")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Acesso não autorizado")
    })
    public ResponseEntity<Page<UserDTO>> findUsers(
            @Parameter(description = "Filtro por parte do nome")
            @RequestParam(required = false) String nome,

            @Parameter(description = "Parâmetros de paginação (page, size, sort)")
            @PageableDefault(sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<UserDTO> Users = userService.usersList(nome, pageable);
        return ResponseEntity.ok(Users);
    }


    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna os detalhes de um usuário específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "401", description = "Acesso não autorizado")
    })
    public ResponseEntity<UserDTO> findUser(
            @Parameter(description = "ID do usuário a ser buscado", example = "1", required = true)
            @PathVariable Long id) {

        UserDTO User = userService.findById(id);
        return ResponseEntity.ok(User);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro durante a exclusão");
        }
    }
}