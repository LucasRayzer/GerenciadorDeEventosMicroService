package service.avaliacao.service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.avaliacao.client.EventClient;
import service.avaliacao.commons.StatusEvento;
import service.avaliacao.dto.AvaliacaoRequisicaoDto;
import service.avaliacao.dto.AvaliacaoRespostaDto;
import service.avaliacao.exception.RecursoNaoEncontradoException;
import service.avaliacao.model.Avaliacao;
import service.avaliacao.repository.AvaliacaoRepository;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final EventClient eventClient;

    @Transactional
    public AvaliacaoRespostaDto criarAvaliacao(AvaliacaoRequisicaoDto requisicaoDto, UUID autorId) {
        // validação antes de criar
        Long eventoId = requisicaoDto.getEventoId();
        EventClient.EventInfo event = eventClient.getEventById(eventoId);

        if (event == null) {
            throw new RecursoNaoEncontradoException("Evento não encontrado com ID: " + eventoId);
        }

        if (event.getStatus() == StatusEvento.CANCELADO) {
            throw new IllegalStateException("Não é possível avaliar um evento que foi cancelado.");
        }
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setNota(requisicaoDto.getNota());
        avaliacao.setComentario(requisicaoDto.getComentario());
        avaliacao.setEventoId(eventoId);
        avaliacao.setAutorId(autorId);
        avaliacao.setDataAvaliacao(LocalDateTime.now());

        Avaliacao avaliacaoSalva = avaliacaoRepository.save(avaliacao);
        return paraRespostaDto(avaliacaoSalva);
    }

    @Transactional(readOnly = true)
    public Page<AvaliacaoRespostaDto> buscarAvaliacoesPorEvento(Long eventoId, Pageable pageable) {
        return avaliacaoRepository.findByEventoId(eventoId, pageable).map(this::paraRespostaDto);    }

    @Transactional(readOnly = true)
    public Page<AvaliacaoRespostaDto> buscarAvaliacoesPorAutor(UUID autorId, Pageable pageable) {
        return avaliacaoRepository.findByAutorId(autorId, pageable).map(this::paraRespostaDto);
    }

    @Transactional
    public void deletarAvaliacao(Long avaliacaoId, UUID autorId) {
        Avaliacao avaliacao = avaliacaoRepository.findById(avaliacaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Avaliação não encontrada com ID: " + avaliacaoId));

        // apenas o próprio autor pode deletar sua avaliação
        if (!avaliacao.getAutorId().equals(autorId)) {
            throw new SecurityException("Usuário não autorizado a deletar esta avaliação.");
        }

        avaliacaoRepository.delete(avaliacao);
    }

    private AvaliacaoRespostaDto paraRespostaDto(Avaliacao avaliacao) {
        AvaliacaoRespostaDto dto = new AvaliacaoRespostaDto();
        dto.setId(avaliacao.getId());
        dto.setNota(avaliacao.getNota());
        dto.setComentario(avaliacao.getComentario());
        dto.setEventoId(avaliacao.getEventoId());
        dto.setAutorId(avaliacao.getAutorId());
        dto.setDataAvaliacao(avaliacao.getDataAvaliacao());
        return dto;
    }
}