package com.users.userservice.dto;

import com.users.userservice.domain.TipoUsuario;

public record UsuarioRequest(
        String nome,
        String email,
        String endereco,
        String telefone,
        String senha,
        TipoUsuario tipo
) {}

