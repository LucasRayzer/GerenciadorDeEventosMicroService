package Auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import model.TipoUsuario;

class TipoUsuarioTest {
    @Test
    void deveConterOsValoresCorretos() {
        // Assert
        assertThat(TipoUsuario.CLIENTE.name()).isEqualTo("CLIENTE");
        assertThat(TipoUsuario.ORGANIZADOR.name()).isEqualTo("ORGANIZADOR");
    }

    @Test
    void deveListarTodosOsValores() {
        // Act
        TipoUsuario[] valores = TipoUsuario.values();

        // Assert
        assertThat(valores).containsExactlyInAnyOrder(TipoUsuario.CLIENTE, TipoUsuario.ORGANIZADOR);
    }
}
