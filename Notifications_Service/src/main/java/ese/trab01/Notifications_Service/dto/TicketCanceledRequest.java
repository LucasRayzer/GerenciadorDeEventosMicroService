// dto/TicketCanceledRequest.java
package ese.trab01.Notifications_Service.dto;

import java.util.UUID;

public record TicketCanceledRequest(
        UUID participantId,
        Long eventId,
        Long ticketId,
        String reason
) {}
