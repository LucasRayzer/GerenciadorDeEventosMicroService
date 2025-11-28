package ese.trab01.Notifications_Service;

import ese.trab01.Notifications_Service.dto.NotificationRequestDto;
import ese.trab01.Notifications_Service.model.NotificationChannel;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NotificationRequestDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void builder_deveConstruirObjetoCorretamente() {
        NotificationRequestDto dto = NotificationRequestDto.builder()
                .recipient("user@example.com")
                .subject("Assunto teste")
                .message("Mensagem de teste")
                .channel(NotificationChannel.EMAIL) // ajusta pro enum real
                .build();

        assertNotNull(dto);
        assertEquals("user@example.com", dto.getRecipient());
        assertEquals("Assunto teste", dto.getSubject());
        assertEquals("Mensagem de teste", dto.getMessage());
        assertEquals(NotificationChannel.EMAIL, dto.getChannel());
    }

    @Test
    void validation_deveDetectarCamposObrigatoriosVazios() {
        NotificationRequestDto dto = new NotificationRequestDto();
        dto.setRecipient("");
        dto.setSubject("");
        dto.setMessage("");
        dto.setChannel(null);

        Set<ConstraintViolation<NotificationRequestDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());

        // Garante que cada campo obrigatório aparece nas violações
        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("recipient")),
                "Deveria ter violação para recipient"
        );
        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("subject")),
                "Deveria ter violação para subject"
        );
        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("message")),
                "Deveria ter violação para message"
        );
        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("channel")),
                "Deveria ter violação para channel"
        );
    }

    @Test
    void validation_deveAceitarObjetoValido() {
        NotificationRequestDto dto = NotificationRequestDto.builder()
                .recipient("user@example.com")
                .subject("Assunto válido")
                .message("Mensagem válida dentro do limite de tamanho.")
                .channel(NotificationChannel.EMAIL) // ajusta pro enum real
                .build();

        Set<ConstraintViolation<NotificationRequestDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "Não deveria haver violações para um DTO válido");
    }
}
