package Auth.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import dto.LoginRequest;

class LoginRequestTest {
    @Test
    void deveTestarGettersESetters() {
        // Arrange
        String usernameEsperado = "joao.silva";
        String senhaEsperada = "senha123";
        LoginRequest dto = new LoginRequest();

        dto.setUsername(usernameEsperado);
        dto.setSenha(senhaEsperada);

        // Assert
        assertThat(dto.getUsername()).isEqualTo(usernameEsperado);
        assertThat(dto.getSenha()).isEqualTo(senhaEsperada);
    }

    @Test
    void deveTestarAllArgsConstructor() {
        // Arrange
        String username = "maria.teste";
        String senha = "test_pass";

        // Act
        LoginRequest dto = new LoginRequest(username, senha);

        // Assert
        assertThat(dto.getUsername()).isEqualTo(username);
        assertThat(dto.getSenha()).isEqualTo(senha);
    }

    @Test
    void deveTestarNoArgsConstructor() {
        // Act
        LoginRequest dto = new LoginRequest();

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.getUsername()).isNull();
        assertThat(dto.getSenha()).isNull();
    }
}
