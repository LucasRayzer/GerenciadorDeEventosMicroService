package Auth.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;

import org.junit.jupiter.api.Test;

import dto.AuthResponse;

public class AuthResponseTest {
    @Test
    void deveTestarGettersESetters() {
        // Arrange
        String tokenEsperado = "novo.token.jwt";
        Date expirationEsperada = new Date(System.currentTimeMillis() + 60000);
        AuthResponse dto = new AuthResponse(null, null);

        // Act
        dto.setToken(tokenEsperado);
        dto.setExpiration(expirationEsperada);

        // Assert
        assertThat(dto.getToken()).isEqualTo(tokenEsperado);
        assertThat(dto.getExpiration()).isEqualTo(expirationEsperada);
    }

    @Test
    void deveTestarAllArgsConstructor() {
        // Arrange
        String token = "teste.token";
        Date expiration = new Date(System.currentTimeMillis() + 120000);

        // Act
        AuthResponse dto = new AuthResponse(token, expiration);

        // Assert
        assertThat(dto.getToken()).isEqualTo(token);
        assertThat(dto.getExpiration()).isEqualTo(expiration);
    }
}
