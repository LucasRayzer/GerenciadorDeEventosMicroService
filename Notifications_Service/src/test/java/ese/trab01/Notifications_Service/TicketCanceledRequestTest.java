package ese.trab01.Notifications_Service;

import ese.trab01.Notifications_Service.dto.TicketCanceledRequest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TicketCanceledRequestTest {

    @Test
    void record_deveConterDadosCorretamente() {
        UUID participantId = UUID.randomUUID();
        Long eventId = 1L;
        Long ticketId = 2L;
        String reason = "Problema no pagamento";

        TicketCanceledRequest request = new TicketCanceledRequest(
                participantId,
                eventId,
                ticketId,
                reason
        );

        assertNotNull(request);
        assertEquals(participantId, request.participantId());
        assertEquals(eventId, request.eventId());
        assertEquals(ticketId, request.ticketId());
        assertEquals(reason, request.reason());
    }
}
