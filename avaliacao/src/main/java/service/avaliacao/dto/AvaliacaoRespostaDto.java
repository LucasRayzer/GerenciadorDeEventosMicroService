package service.avaliacao.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AvaliacaoRespostaDto {
    private Long id;
    private Integer nota;
    private String comentario;
    private Long eventoId;
    private UUID autorId;
    private LocalDateTime dataAvaliacao;

}