package service.eventos;
import org.junit.jupiter.api.Test;

import service.eventos.dto.EventoRequisicaoDto;

import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
public class EventoRequisicaodtoTest {
    @Test
    void deveTestarGettersESetters() {
        // Arrange
        String nome = "Show de Rock";
        LocalDateTime data = LocalDateTime.now().plusDays(7);
        Integer capacidade = 100;
        
        // Act
        EventoRequisicaoDto dto = new EventoRequisicaoDto();
        dto.setNome(nome);
        dto.setDescricao("Nova turnê");
        dto.setLocalizacao("Arena");
        dto.setData(data);
        dto.setCapacidade(capacidade);
        dto.setCategoriaId(1L);
        
        // Assert
        assertThat(dto.getNome()).isEqualTo(nome);
        assertThat(dto.getData()).isEqualTo(data);
        assertThat(dto.getCapacidade()).isEqualTo(capacidade);
        assertThat(dto.getCategoriaId()).isEqualTo(1L);
    }
}
