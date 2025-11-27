package Auth.service;

import org.junit.jupiter.api.Test;

import dto.RegisterRequest;
import model.TipoUsuario;
import static org.assertj.core.api.Assertions.assertThat;

class RegisterRequestTest {

    @Test
    void deveTestarGettersESetters() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        String nome = "Pedro Teste";
        String email = "pedro@email.com";
        String senha = "pass";
        TipoUsuario tipo = TipoUsuario.ORGANIZADOR;

        // Act
        request.setNome(nome);
        request.setEmail(email);
        request.setEndereco("Rua A");
        request.setTelefone("1111-2222");
        request.setSenha(senha);
        request.setTipo(tipo);

        // Assert
        assertThat(request.getNome()).isEqualTo(nome);
        assertThat(request.getEmail()).isEqualTo(email);
        assertThat(request.getEndereco()).isEqualTo("Rua A");
        assertThat(request.getTelefone()).isEqualTo("1111-2222");
        assertThat(request.getSenha()).isEqualTo(senha);
        assertThat(request.getTipo()).isEqualTo(tipo);
    }
}
