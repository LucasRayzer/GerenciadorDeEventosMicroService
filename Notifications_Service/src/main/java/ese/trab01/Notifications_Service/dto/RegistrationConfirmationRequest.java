package ese.trab01.Notifications_Service.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RegistrationConfirmationRequest(
        UUID participantId
) {}
