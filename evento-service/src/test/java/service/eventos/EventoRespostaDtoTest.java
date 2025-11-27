package service.eventos;
import org.junit.jupiter.api.Test;
import service.eventos.model.Evento;
import service.eventos.model.StatusEvento;
import service.eventos.dto.EventoRespostaDto;
import service.eventos.dto.CategoriaDto; // Importa CategoriaDto
import service.eventos.model.Categoria; // Importa Categoria

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

class EventoRespostaDtoTest {

    // Helper para criar uma entidade Evento mock
    private Evento criarEventoMock(int capacidade, int inscritos) {
        Evento evento = new Evento();
        evento.setId(1L);
        evento.setNome("Evento de Teste");
        evento.setLocalizacao("Online");
        evento.setOrganizerId(UUID.randomUUID());
        evento.setStatus(StatusEvento.ATIVO);
        evento.setData(LocalDateTime.now().plusDays(1));
        evento.setCapacidade(capacidade);

        Categoria cat = new Categoria();
        cat.setId(99L);
        cat.setNome("Mock Cat");
        evento.setCategoria(cat);

        Set<UUID> participantes = new HashSet<>();
        for (int i = 0; i < inscritos; i++) {
            participantes.add(UUID.randomUUID());
        }
        evento.setParticipanteId(participantes);
        return evento;
    }

    @Test
    void deveCalcularVagasCorretamenteQuandoHaVagas() {
        // Capacidade: 10, Inscritos: 3 -> Vagas: 7
        Evento evento = criarEventoMock(10, 3);
        
        EventoRespostaDto dto = new EventoRespostaDto(evento);

        assertThat(dto.getVagas()).isEqualTo(7);
        assertThat(dto.getCapacidade()).isEqualTo(10);
    }

    @Test
    void deveCalcularZeroVagasQuandoCapacidadeMaximaAtingida() {
        // Capacidade: 5, Inscritos: 5 -> Vagas: 0
        Evento evento = criarEventoMock(5, 5); 

        EventoRespostaDto dto = new EventoRespostaDto(evento);

        assertThat(dto.getVagas()).isEqualTo(0);
        assertThat(dto.getCapacidade()).isEqualTo(5);
    }
    
    @Test
    void deveCalcularZeroVagasQuandoExcederCapacidade() {
        // Capacidade: 5, Inscritos: 8 -> Vagas: 0 (não negativo)
        Evento evento = criarEventoMock(5, 8); 

        EventoRespostaDto dto = new EventoRespostaDto(evento);

        assertThat(dto.getVagas()).isEqualTo(0); // Usa Math.max(0, ...)
    }

    @Test
    void deveSetarVagasComoNullQuandoCapacidadeForNull() {
        Evento evento = criarEventoMock(0, 0);
        evento.setCapacidade(null); // Capacidade nula

        EventoRespostaDto dto = new EventoRespostaDto(evento);

        assertNull(dto.getVagas());
    }

    @Test
    void deveMapearCamposDaEntidadeCorretamente() {
        Evento evento = criarEventoMock(1, 0);
        UUID organizerId = UUID.randomUUID();
        evento.setOrganizerId(organizerId);

        EventoRespostaDto dto = new EventoRespostaDto(evento);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getNome()).isEqualTo("Evento de Teste");
        assertThat(dto.getOrganizerId()).isEqualTo(organizerId);
        assertThat(dto.getStatus()).isEqualTo(StatusEvento.ATIVO);
        assertThat(dto.getCategoria()).isNull(); // Construtor com Evento não preenche CategoriaDto
    }

    @Test
    void deveTestarGettersESetters() {
        // Testa o construtor AllArgsConstructor e setters/getters
        EventoRespostaDto dto = new EventoRespostaDto(
            2L, 
            "Nome Teste", 
            "Desc Teste", 
            "Local Teste", 
            LocalDateTime.now(), 
            100, 
            50, 
            StatusEvento.ATIVO, 
            UUID.randomUUID(), 
            "Org Nome", 
            new CategoriaDto()
        );

        assertThat(dto.getNome()).isEqualTo("Nome Teste");
        dto.setVagas(10);
        assertThat(dto.getVagas()).isEqualTo(10);
    }
}