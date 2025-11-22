package ese.trab01.Notifications_Service.dto;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
public record EventReminderRequest(
        UUID participantId,
        Long eventId,
        OffsetDateTime eventDateTime,
        String eventName
) {}
