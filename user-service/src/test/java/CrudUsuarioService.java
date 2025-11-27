
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.users.userservice.application.CrudUsuarioService;
import com.users.userservice.domain.TipoUsuario;
import com.users.userservice.domain.Usuario;
import com.users.userservice.repository.UsuarioRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

class CrudUsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CrudUsuarioService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("criar encodes password when provided and non-blank")
    void criarEncodesPassword() {
        Usuario input = new Usuario("Ana", "ana@test.com", "Rua A", "9999", "raw", TipoUsuario.CLIENTE);
        when(passwordEncoder.encode("raw")).thenReturn("hashed");
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Usuario saved = service.criar(input);

        assertThat(saved.getSenha()).isEqualTo("hashed");
        verify(passwordEncoder).encode("raw");
        verify(repository).save(saved);
    }

    @Test
    @DisplayName("criar skips encoding when password is blank")
    void criarSkipsEncodingOnBlank() {
        Usuario input = new Usuario("Ana", "ana@test.com", "Rua A", "9999", "   ", TipoUsuario.CLIENTE);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Usuario saved = service.criar(input);

        assertThat(saved.getSenha()).isEqualTo("   ");
        verifyNoInteractions(passwordEncoder);
        verify(repository).save(saved);
    }

    @Test
    @DisplayName("buscarPorId returns Optional from repository")
    void buscarPorId() {
        UUID id = UUID.randomUUID();
        Usuario usuario = new Usuario("Ana", "ana@test.com", "Rua A", "9999", "pwd", TipoUsuario.CLIENTE);
        when(repository.findById(id)).thenReturn(Optional.of(usuario));

        Optional<Usuario> found = service.buscarPorId(id);

        assertThat(found).contains(usuario);
        verify(repository).findById(id);
    }

    @Test
    @DisplayName("atualizar updates fields when entity exists")
    void atualizarUpdatesFields() {
        UUID id = UUID.randomUUID();
        Usuario existente = new Usuario("Old", "old@test.com", "Rua X", "1111", "oldpwd", TipoUsuario.CLIENTE);
        Usuario novo = new Usuario("New", "new@test.com", "Rua Y", "2222", "newpwd", TipoUsuario.ORGANIZADOR);

        when(repository.findById(id)).thenReturn(Optional.of(existente));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Usuario atualizado = service.atualizar(id, novo);

        assertThat(atualizado.getNome()).isEqualTo("New");
        assertThat(atualizado.getEmail()).isEqualTo("new@test.com");
        assertThat(atualizado.getEndereco()).isEqualTo("Rua Y");
        assertThat(atualizado.getTelefone()).isEqualTo("2222");
        assertThat(atualizado.getSenha()).isEqualTo("newpwd");
        verify(repository).save(existente);
    }

    @Test
    @DisplayName("atualizar throws when entity is missing")
    void atualizarThrowsWhenMissing() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.atualizar(id, new Usuario()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    @DisplayName("listar delegates to repository")
    void listar() {
        when(repository.findAll()).thenReturn(List.of());

        List<Usuario> result = service.listar();

        assertThat(result).isEmpty();
        verify(repository).findAll();
    }

    @Test
    @DisplayName("deletar delegates to repository")
    void deletar() {
        UUID id = UUID.randomUUID();

        service.deletar(id);

        verify(repository).deleteById(id);
    }
}
