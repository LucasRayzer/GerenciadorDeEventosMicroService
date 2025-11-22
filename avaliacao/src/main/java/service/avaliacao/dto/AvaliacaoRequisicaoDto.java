package service.avaliacao.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AvaliacaoRequisicaoDto {

    @NotNull(message = "A nota é obrigatória")
    @Min(value = 1, message = "A nota mínima é 1")
    @Max(value = 5, message = "A nota máxima é 5")
    private Integer nota;

    @NotBlank(message = "O comentário não pode ser vazio")
    private String comentario;

    @NotNull(message = "O ID do evento é obrigatório")
    private Long eventoId;
}