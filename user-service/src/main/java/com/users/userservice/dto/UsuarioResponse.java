package com.users.userservice.dto;

import com.users.userservice.domain.TipoUsuario;
import java.util.UUID;

public record UsuarioResponse(
        UUID id,
        String nome,
        String email,
        String endereco,
        String telefone,
        TipoUsuario tipo
) {}

