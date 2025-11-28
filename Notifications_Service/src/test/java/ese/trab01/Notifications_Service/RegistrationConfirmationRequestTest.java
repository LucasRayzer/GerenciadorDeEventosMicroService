package ese.trab01.Notifications_Service;

import ese.trab01.Notifications_Service.dto.RegistrationConfirmationRequest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RegistrationConfirmationRequestTest {

    @Test
    void builder_deveConstruirObjetoCorretamente() {
        UUID participantId = UUID.randomUUID();

        RegistrationConfirmationRequest request = RegistrationConfirmationRequest.builder()
                .participantId(participantId)
                .build();

        assertNotNull(request);
        assertEquals(participantId, request.participantId());
    }
}
