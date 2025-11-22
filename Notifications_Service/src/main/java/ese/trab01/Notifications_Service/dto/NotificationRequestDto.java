package ese.trab01.Notifications_Service.dto;

import ese.trab01.Notifications_Service.model.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequestDto {

    @NotBlank(message = "O destinatário é obrigatório.")
    private String recipient;

    @NotBlank(message = "O assunto é obrigatório.")
    @Size(max = 120, message = "O assunto deve ter no máximo 120 caracteres.")
    private String subject;

    @NotBlank(message = "A mensagem é obrigatória.")
    @Size(max = 5000, message = "A mensagem deve ter no máximo 5000 caracteres.")
    private String message;

    @NotNull(message = "O canal é obrigatório.")
    private NotificationChannel channel;

}