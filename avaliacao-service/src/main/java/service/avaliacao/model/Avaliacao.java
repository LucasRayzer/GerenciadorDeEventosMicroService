package service.avaliacao.model;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="avaliacoes")
@Data
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer nota; // Ex: 1 a 5

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @Column(nullable = false)
    private Long eventoId;

    @Column(nullable = false)
    private UUID autorId; // ID do participante que fez a avaliação

    @Column(nullable = false)
    private LocalDateTime dataAvaliacao;
}