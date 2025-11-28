package ese.trab01.Notifications_Service;

import ese.trab01.Notifications_Service.dto.EventReminderRequest;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EventReminderRequestTest {

    @Test
    void builder_deveConstruirObjetoCorretamente() {
        UUID participantId = UUID.randomUUID();
        Long eventId = 1L;
        OffsetDateTime dateTime = OffsetDateTime.of(
                2025, 1, 1,
                20, 0, 0, 0,
                ZoneOffset.UTC
        );
        String eventName = "Evento Teste";

        EventReminderRequest request = EventReminderRequest.builder()
                .participantId(participantId)
                .eventId(eventId)
                .eventDateTime(dateTime)
                .eventName(eventName)
                .build();

        assertNotNull(request);
        assertEquals(participantId, request.participantId());
        assertEquals(eventId, request.eventId());
        assertEquals(dateTime, request.eventDateTime());
        assertEquals(eventName, request.eventName());
    }
}
