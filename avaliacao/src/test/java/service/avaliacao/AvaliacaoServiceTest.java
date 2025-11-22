package service.avaliacao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import service.avaliacao.client.EventClient;
import service.avaliacao.dto.AvaliacaoRequisicaoDto;
import service.avaliacao.exception.RecursoNaoEncontradoException;
import service.avaliacao.model.Avaliacao;
import service.avaliacao.repository.AvaliacaoRepository;
import service.avaliacao.service.AvaliacaoService;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvaliacaoServiceTest {

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private EventClient eventClient;

    @InjectMocks
    private AvaliacaoService avaliacaoService;

    @Test
    void deveCriarAvaliacaoComSucesso() {
        UUID autorId = UUID.randomUUID();

        AvaliacaoRequisicaoDto requisicao = new AvaliacaoRequisicaoDto();
        requisicao.setNota(5);
        requisicao.setComentario("Ótimo evento.");
        requisicao.setEventoId(10L);

        EventClient.EventInfo mockEvento = new EventClient.EventInfo();
        mockEvento.setId(10L);
        mockEvento.setNome("Evento de Teste");

        when(eventClient.getEventById(10L)).thenReturn(mockEvento);

        when(avaliacaoRepository.save(any(Avaliacao.class))).thenAnswer(invocation -> {
            Avaliacao avaliacaoSalva = invocation.getArgument(0);
            avaliacaoSalva.setId(1L);
            return avaliacaoSalva;
        });

        var resposta = avaliacaoService.criarAvaliacao(requisicao, autorId);

        assertThat(resposta).isNotNull();
        assertThat(resposta.getId()).isEqualTo(1L);
        assertThat(resposta.getNota()).isEqualTo(5);
        assertThat(resposta.getAutorId()).isEqualTo(autorId);
        assertThat(resposta.getEventoId()).isEqualTo(10L);

        verify(avaliacaoRepository).save(any(Avaliacao.class));
        verify(eventClient).getEventById(10L);
    }



    @Test
    void deveDeletarAvaliacaoComSucesso() {
        Long avaliacaoId = 1L;
        UUID autorId = UUID.randomUUID();
        Avaliacao avaliacaoMock = criarAvaliacaoMock(avaliacaoId, autorId);

        when(avaliacaoRepository.findById(avaliacaoId)).thenReturn(Optional.of(avaliacaoMock));
        doNothing().when(avaliacaoRepository).delete(avaliacaoMock);

        avaliacaoService.deletarAvaliacao(avaliacaoId, autorId);

        verify(avaliacaoRepository).findById(avaliacaoId);
        verify(avaliacaoRepository).delete(avaliacaoMock);
    }

    @Test
    void naoDeveDeletarAvaliacaoSeNaoEncontrar() {
        Long avaliacaoId = 99L;
        UUID autorId = UUID.randomUUID();

        when(avaliacaoRepository.findById(avaliacaoId)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            avaliacaoService.deletarAvaliacao(avaliacaoId, autorId);
        });

        verify(avaliacaoRepository, never()).delete(any());
    }

    @Test
    void naoDeveDeletarAvaliacaoDeOutroAutor() {
        Long avaliacaoId = 1L;
        UUID autorIdCorreto = UUID.randomUUID();
        UUID autorIdIncorreto = UUID.randomUUID();
        Avaliacao avaliacaoMock = criarAvaliacaoMock(avaliacaoId, autorIdCorreto);


        when(avaliacaoRepository.findById(avaliacaoId)).thenReturn(Optional.of(avaliacaoMock));

        assertThrows(SecurityException.class, () -> {
            avaliacaoService.deletarAvaliacao(avaliacaoId, autorIdIncorreto);
        });

        verify(avaliacaoRepository, never()).delete(any());
    }


    private Avaliacao criarAvaliacaoMock(Long id, UUID autorId) {

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setId(id);
        avaliacao.setAutorId(autorId);
        avaliacao.setEventoId(10L);
        avaliacao.setNota(4);
        avaliacao.setComentario("Comentário de teste");
        avaliacao.setDataAvaliacao(LocalDateTime.now());
        return avaliacao;
    }
}