package service.avaliacao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.avaliacao.model.Avaliacao;

import java.util.UUID;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    Page<Avaliacao> findByEventoId(Long eventoId, Pageable pageable);

    Page<Avaliacao> findByAutorId(UUID autorId, Pageable pageable);
}
