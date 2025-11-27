package service.eventos;

import org.junit.jupiter.api.Test;

import service.eventos.dto.CategoriaDto;

import static org.assertj.core.api.Assertions.assertThat;

class CategoriaDtoTest {

    @Test
    void deveTestarGettersESetters() {
        // Arrange
        Long idEsperado = 1L;
        String nomeEsperado = "Música";
        CategoriaDto dto = new CategoriaDto();
        
        // Act
        dto.setId(idEsperado);
        dto.setNome(nomeEsperado);
        
        // Assert
        assertThat(dto.getId()).isEqualTo(idEsperado);
        assertThat(dto.getNome()).isEqualTo(nomeEsperado);
    }
}