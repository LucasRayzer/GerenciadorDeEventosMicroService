package service.avaliacao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import service.avaliacao.controller.AvaliacaoController;
import service.avaliacao.dto.AvaliacaoRequisicaoDto;
import service.avaliacao.dto.AvaliacaoRespostaDto;
import service.avaliacao.exception.RecursoNaoEncontradoException;
import service.avaliacao.service.AvaliacaoService;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AvaliacaoController.class)
class AvaliacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AvaliacaoService avaliacaoService;

    private UUID autorId;
    private UUID organizadorId;

    @BeforeEach
    void setUp() {
        autorId = UUID.randomUUID();
        organizadorId = UUID.randomUUID();
    }

    private AvaliacaoRequisicaoDto criarRequisicaoValida() {
        AvaliacaoRequisicaoDto dto = new AvaliacaoRequisicaoDto();
        dto.setNota(5);
        dto.setComentario("Ótima experiência!");
        dto.setEventoId(1L);
        return dto;
    }


    @Test
    void deveCriarAvaliacaoComSucessoERetornarCreated() throws Exception {
        AvaliacaoRequisicaoDto requisicao = criarRequisicaoValida();
        AvaliacaoRespostaDto resposta = new AvaliacaoRespostaDto();
        resposta.setId(1L);
        resposta.setAutorId(autorId);

        when(avaliacaoService.criarAvaliacao(any(AvaliacaoRequisicaoDto.class), eq(autorId))).thenReturn(resposta);

        mockMvc.perform(post("/avaliacoes/nova-avaliacao")
                        .header("X-User-Id", autorId.toString())
                        .header("X-User-Roles", "CLIENTE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicao)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void naoDeveCriarAvaliacaoSeNaoForCliente() throws Exception {
        AvaliacaoRequisicaoDto requisicao = criarRequisicaoValida();

        mockMvc.perform(post("/avaliacoes/nova-avaliacao")
                        .header("X-User-Id", organizadorId.toString())
                        .header("X-User-Roles", "ORGANIZADOR") 
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicao)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Apenas CLIENTES podem criar avaliações."));
    }

    @Test
    void naoDeveCriarAvaliacaoSeNaoAutenticado() throws Exception {
        AvaliacaoRequisicaoDto requisicao = criarRequisicaoValida();

        mockMvc.perform(post("/avaliacoes/nova-avaliacao")
                        .header("X-User-Roles", "CLIENTE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicao)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornarBadRequestComDadosInvalidos() throws Exception {
        AvaliacaoRequisicaoDto requisicaoInvalida = new AvaliacaoRequisicaoDto();

        mockMvc.perform(post("/avaliacoes/nova-avaliacao")
                        .header("X-User-Id", autorId.toString())
                        .header("X-User-Roles", "CLIENTE")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requisicaoInvalida)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void deveBuscarMinhasAvaliacoesComSucesso() throws Exception {
        Page<AvaliacaoRespostaDto> paginaMock = new PageImpl<>(List.of(new AvaliacaoRespostaDto()));
        when(avaliacaoService.buscarAvaliacoesPorAutor(eq(autorId), any(Pageable.class))).thenReturn(paginaMock);

        mockMvc.perform(get("/avaliacoes/minhas-avaliacoes")
                        .header("X-User-Id", autorId.toString())
                        .header("X-User-Roles", "CLIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void naoDeveBuscarMinhasAvaliacoesSeNaoForCliente() throws Exception {
        mockMvc.perform(get("/avaliacoes/minhas-avaliacoes")
                        .header("X-User-Id", organizadorId.toString())
                        .header("X-User-Roles", "ORGANIZADOR"))
                .andExpect(status().isForbidden());
    }


    @Test
    void deveDeletarAvaliacaoComSucessoERetornarNoContent() throws Exception {
        Long avaliacaoId = 5L;
        doNothing().when(avaliacaoService).deletarAvaliacao(avaliacaoId, autorId);

        mockMvc.perform(delete("/avaliacoes/{avaliacaoId}", avaliacaoId)
                        .header("X-User-Id", autorId.toString())
                        .header("X-User-Roles", "CLIENTE"))
                .andExpect(status().isNoContent());
    }

    @Test
    void naoDeveDeletarAvaliacaoSeNaoForCliente() throws Exception {
        Long avaliacaoId = 5L;

        mockMvc.perform(delete("/avaliacoes/{avaliacaoId}", avaliacaoId)
                        .header("X-User-Id", organizadorId.toString())
                        .header("X-User-Roles", "ORGANIZADOR"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornarNotFoundAoDeletarAvaliacaoInexistente() throws Exception {
        Long avaliacaoId = 99L;
        doThrow(new RecursoNaoEncontradoException("Não encontrado")).when(avaliacaoService).deletarAvaliacao(avaliacaoId, autorId);
        
        mockMvc.perform(delete("/avaliacoes/{avaliacaoId}", avaliacaoId)
                        .header("X-User-Id", autorId.toString())
                        .header("X-User-Roles", "CLIENTE"))
                .andExpect(status().isNotFound()); 
    }
}