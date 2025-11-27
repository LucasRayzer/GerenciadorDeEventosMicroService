package service.eventos;

import org.junit.jupiter.api.Test;

import service.eventos.dto.CategoriaRequisicaoDto;

import static org.assertj.core.api.Assertions.assertThat;

class CategoriaRequisicaoDtoTest {

    @Test
    void deveTestarGettersESetters() {
        // Arrange
        String nomeEsperado = "Esportes";
        CategoriaRequisicaoDto dto = new CategoriaRequisicaoDto();
        
        // Act
        dto.setNome(nomeEsperado);
        
        // Assert
        assertThat(dto.getNome()).isEqualTo(nomeEsperado);
    }
}
