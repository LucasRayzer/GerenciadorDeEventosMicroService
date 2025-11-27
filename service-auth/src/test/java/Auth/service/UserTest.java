package Auth.service;
import org.junit.jupiter.api.Test;

import model.User;

import java.util.Date;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

import model.TipoUsuario;
class UserTest {

    private final UUID idMock = UUID.randomUUID();
    private final Date expirationMock = new Date(System.currentTimeMillis() + 600000);

    @Test
    void deveTestarGettersESetters() {
        // Arrange
        User user = new User();
        
        // Act
        user.setId(idMock);
        user.setTipo(TipoUsuario.CLIENTE);
        user.setUsername("user@email.com");
        user.setSenha("hashed_pass");
        user.setToken("valid.jwt.token");
        user.setTokenExpiration(expirationMock);

        // Assert
        assertThat(user.getId()).isEqualTo(idMock);
        assertThat(user.getTipo()).isEqualTo(TipoUsuario.CLIENTE);
        assertThat(user.getUsername()).isEqualTo("user@email.com");
        assertThat(user.getSenha()).isEqualTo("hashed_pass");
        assertThat(user.getToken()).isEqualTo("valid.jwt.token");
        assertThat(user.getTokenExpiration()).isEqualTo(expirationMock);
    }

    @Test
    void deveTestarAllArgsConstructor() {
        // Act
        User user = new User(
            idMock,
            TipoUsuario.ORGANIZADOR,
            "org@email.com",
            "hashed_pass",
            "token.jwt.org",
            expirationMock
        );

        // Assert
        assertThat(user.getId()).isEqualTo(idMock);
        assertThat(user.getTipo()).isEqualTo(TipoUsuario.ORGANIZADOR);
        assertThat(user.getUsername()).isEqualTo("org@email.com");
    }

    @Test
    void deveTestarNoArgsConstructor() {
        // Act
        User user = new User();

        // Assert
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isNull();
    }
}