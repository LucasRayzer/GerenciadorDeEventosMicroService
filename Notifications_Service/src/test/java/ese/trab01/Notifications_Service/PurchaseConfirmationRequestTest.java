package ese.trab01.Notifications_Service;

import ese.trab01.Notifications_Service.dto.PurchaseConfirmationRequest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PurchaseConfirmationRequestTest {

    @Test
    void builder_deveConstruirObjetoCorretamente() {
        UUID participantId = UUID.randomUUID();
        Long eventId = 1L;
        Long ticketId = 2L;

        PurchaseConfirmationRequest request = PurchaseConfirmationRequest.builder()
                .participantId(participantId)
                .eventId(eventId)
                .ticketId(ticketId)
                .build();

        assertNotNull(request);
        assertEquals(participantId, request.participantId());
        assertEquals(eventId, request.eventId());
        assertEquals(ticketId, request.ticketId());
    }
}
