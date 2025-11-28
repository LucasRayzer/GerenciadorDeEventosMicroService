package ese.trab01.Notifications_Service;

import ese.trab01.Notifications_Service.dto.NotificationResponseDto;
import ese.trab01.Notifications_Service.model.NotificationChannel;
import ese.trab01.Notifications_Service.model.NotificationStatus;
import ese.trab01.Notifications_Service.model.NotificationType;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class NotificationResponseDtoTest {

    @Test
    void record_deveConterDadosCorretamente() {
        Long id = 10L;
        NotificationType type = NotificationType.PURCHASE_CONFIRMATION; // ajusta pro enum real
        NotificationChannel channel = NotificationChannel.EMAIL;        // idem
        NotificationStatus status = NotificationStatus.SENT;            // idem

        UUID participantId = UUID.randomUUID();
        Long eventId = 1L;
        Long ticketId = 2L;
        String recipient = "user@example.com";
        String subject = "Assunto teste";
        String message = "Mensagem teste";

        OffsetDateTime createdAt = OffsetDateTime.of(
                2025, 1, 1,
                10, 0, 0, 0,
                ZoneOffset.UTC
        );
        OffsetDateTime sentAt = createdAt.plusMinutes(5);

        NotificationResponseDto dto = new NotificationResponseDto(
                id,
                type,
                channel,
                status,
                participantId,
                eventId,
                ticketId,
                recipient,
                subject,
                message,
                createdAt,
                sentAt
        );

        assertNotNull(dto);
        assertEquals(id, dto.id());
        assertEquals(type, dto.type());
        assertEquals(channel, dto.channel());
        assertEquals(status, dto.status());
        assertEquals(participantId, dto.participantId());
        assertEquals(eventId, dto.eventId());
        assertEquals(ticketId, dto.ticketId());
        assertEquals(recipient, dto.recipient());
        assertEquals(subject, dto.subject());
        assertEquals(message, dto.message());
        assertEquals(createdAt, dto.createdAt());
        assertEquals(sentAt, dto.sentAt());
    }
}
