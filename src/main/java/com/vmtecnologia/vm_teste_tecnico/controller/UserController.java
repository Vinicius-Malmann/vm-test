package com.vmtecnologia.vm_teste_tecnico.controller;

import com.vmtecnologia.vm_teste_tecnico.dto.CreateUserDTO;
import com.vmtecnologia.vm_teste_tecnico.dto.UserDTO;
import com.vmtecnologia.vm_teste_tecnico.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/Users")
@Tag(name = "Usuários", description = "Operações relacionadas a usuários")
public class UserController {

    private final UserService UserService;

    public UserController(UserService UserService) {
        this.UserService = UserService;
    }

    @PostMapping
    @Operation(summary = "Criar novo usuário", description = "Registra um novo usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos fornecidos"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado")
    })
    public ResponseEntity<UserDTO> criarUser(
            @Valid @RequestBody @Parameter(description = "Dados do usuário a ser criado", required = true)
            CreateUserDTO UserDTO) {
        UserDTO novoUser = UserService.criarUser(UserDTO);
        return ResponseEntity.status(201).body(novoUser);
    }

    @GetMapping
    @Operation(summary = "Listar usuários", description = "Retorna lista paginada de usuários com filtro opcional por nome")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Acesso não autorizado")
    })
    public ResponseEntity<Page<UserDTO>> listarUsers(
            @Parameter(description = "Filtro por parte do nome (opcional)")
            @RequestParam(required = false) String nome,

            @Parameter(description = "Parâmetros de paginação (page, size, sort)")
            @PageableDefault(sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<UserDTO> Users = UserService.listarUsers(nome, pageable);
        return ResponseEntity.ok(Users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna os detalhes de um usuário específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
            @ApiResponse(responseCode = "401", description = "Acesso não autorizado")
    })
    public ResponseEntity<UserDTO> buscarUser(
            @Parameter(description = "ID do usuário a ser buscado", example = "1", required = true)
            @PathVariable Long id) {

        UserDTO User = UserService.buscarPorId(id);
        return ResponseEntity.ok(User);
    }
}