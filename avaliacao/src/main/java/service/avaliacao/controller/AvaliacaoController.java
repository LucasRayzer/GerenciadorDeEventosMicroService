package service.avaliacao.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.avaliacao.dto.AvaliacaoRequisicaoDto;
import service.avaliacao.dto.AvaliacaoRespostaDto;
import service.avaliacao.service.AvaliacaoService;

import java.util.UUID;

@RestController
@RequestMapping("/avaliacoes")
@RequiredArgsConstructor
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    @PostMapping("/nova-avaliacao")
    public ResponseEntity<?> criarAvaliacao(
            @Valid @RequestBody AvaliacaoRequisicaoDto requisicaoDto,
            @RequestHeader(value = "X-User-Id", required = false) UUID autorId,
            @RequestHeader(value = "X-User-Roles", required = false) String rolesCsv
    ) {
        if (autorId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado.");
        }
        if (!hasRole(rolesCsv, "CLIENTE")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Apenas CLIENTES podem criar avaliações.");
        }

        AvaliacaoRespostaDto avaliacaoCriada = avaliacaoService.criarAvaliacao(requisicaoDto, autorId);
        return new ResponseEntity<>(avaliacaoCriada, HttpStatus.CREATED);
    }


    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<Page<AvaliacaoRespostaDto>> buscarAvaliacoesPorEvento(
            @PathVariable Long eventoId, Pageable pageable
    ) {
        Page<AvaliacaoRespostaDto> avaliacoes = avaliacaoService.buscarAvaliacoesPorEvento(eventoId, pageable);
        return ResponseEntity.ok(avaliacoes);
    }


    @GetMapping("/minhas-avaliacoes")
    public ResponseEntity<?> buscarMinhasAvaliacoes(
            Pageable pageable,
            @RequestHeader(value = "X-User-Id", required = false) UUID autorId,
            @RequestHeader(value = "X-User-Roles", required = false) String rolesCsv
    ) {
        if (autorId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado.");
        }

        if (!hasRole(rolesCsv, "CLIENTE")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Apenas CLIENTES podem ver 'minhas avaliações'.");
        }

        Page<AvaliacaoRespostaDto> avaliacoes = avaliacaoService.buscarAvaliacoesPorAutor(autorId, pageable);
        return ResponseEntity.ok(avaliacoes);
    }


    @DeleteMapping("/{avaliacaoId}")
    public ResponseEntity<?> deletarAvaliacao(
            @PathVariable Long avaliacaoId,
            @RequestHeader(value = "X-User-Id", required = false) UUID autorId,
            @RequestHeader(value = "X-User-Roles", required = false) String rolesCsv
    ) {
        if (autorId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado.");
        }
        if (!hasRole(rolesCsv, "CLIENTE")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Apenas CLIENTES podem deletar avaliações.");
        }

        avaliacaoService.deletarAvaliacao(avaliacaoId, autorId);
        return ResponseEntity.noContent().build();
    }

    private boolean hasRole(String rolesCsv, String role) {
        if (rolesCsv == null || rolesCsv.isBlank()) return false;
        for (String r : rolesCsv.split(",")) {
            if (role.equalsIgnoreCase(r.trim())) return true;
        }
        return false;
    }
}