package ese.trab01.Notifications_Service.dto;

import ese.trab01.Notifications_Service.model.NotificationChannel;
import ese.trab01.Notifications_Service.model.NotificationStatus;
import ese.trab01.Notifications_Service.model.NotificationType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NotificationResponseDto(
        Long id,
        NotificationType type,
        NotificationChannel channel,
        NotificationStatus status,
        UUID participantId,
        Long eventId,
        Long ticketId,
        String recipient,
        String subject,
        String message,
        OffsetDateTime createdAt,
        OffsetDateTime sentAt
) {}
