package com.users.userservice.controller;

import com.users.userservice.application.CrudUsuarioService;
import com.users.userservice.domain.Usuario;
import com.users.userservice.dto.UsuarioRequest;
import com.users.userservice.dto.UsuarioResponse;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final CrudUsuarioService crudUsuarioUseCase;

    public UsuarioController(CrudUsuarioService crudUsuarioUseCase) {
        this.crudUsuarioUseCase = crudUsuarioUseCase;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> criar(@RequestBody UsuarioRequest usuario) {
        Usuario criado = crudUsuarioUseCase.criar(toEntity(usuario));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(criado));
    }

    @GetMapping("/busca")
    public ResponseEntity<UsuarioResponse> buscarPorId(@RequestHeader(value = "X-User-Id", required = false) UUID userId)
    {
        return crudUsuarioUseCase
                .buscarPorId(userId)
                .map(u -> ResponseEntity.ok(toResponse(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private Usuario toEntity(UsuarioRequest request) {
        if (request.tipo() != null) {
            return new Usuario(
                    request.nome(),
                    request.email(),
                    request.endereco(),
                    request.telefone(),
                    request.senha(),
                    request.tipo()
            );
        }
        // default type handled by entity constructor (CLIENTE)
        return new Usuario(
                request.nome(),
                request.email(),
                request.endereco(),
                request.telefone(),
                request.senha()
        );
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        //TipoUsuario tipo = usuario.getTipo();
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getEndereco(),
                usuario.getTelefone(),
                usuario.getTipo()
        );
    }
}
