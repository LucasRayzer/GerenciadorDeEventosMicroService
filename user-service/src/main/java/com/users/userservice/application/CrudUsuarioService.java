package com.users.userservice.application;

import com.users.userservice.domain.Usuario;
import com.users.userservice.repository.UsuarioRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CrudUsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public CrudUsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario criar(Usuario usuario) {
        if (usuario.getSenha() != null && !usuario.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }
        return repository.save(usuario);
    }

    public Optional<Usuario> buscarPorId(UUID id) {
        return repository.findById(id);
    }

    public List<Usuario> listar() {
        return repository.findAll();
    }

    public Usuario atualizar(UUID id, Usuario usuario) {
        Usuario existente = repository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Usuário não encontrado: " + id));

        existente.setNome(usuario.getNome());
        existente.setEmail(usuario.getEmail());
        existente.setEndereco(usuario.getEndereco());
        existente.setTelefone(usuario.getTelefone());
        existente.setSenha(usuario.getSenha());

        return repository.save(existente);
    }

    public void deletar(UUID id) {
        repository.deleteById(id);
    }
}
