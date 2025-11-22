package ese.trab01.Notifications_Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ese.trab01.Notifications_Service.controller.NotificationController;
import ese.trab01.Notifications_Service.dto.*;
import ese.trab01.Notifications_Service.model.NotificationChannel;
import ese.trab01.Notifications_Service.model.NotificationStatus;
import ese.trab01.Notifications_Service.model.NotificationType;
import ese.trab01.Notifications_Service.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class NotificationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper om;

    @MockitoBean
    private NotificationService service;

    private NotificationResponseDto sampleDto(long id) {
        UUID participanteId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        return new NotificationResponseDto(
                id,
                NotificationType.PURCHASE_CONFIRMATION,
                NotificationChannel.EMAIL,
                NotificationStatus.PENDING,
                participanteId,
                1L,
                10L,
                null,
                "Assunto",
                "Mensagem",
                OffsetDateTime.now(),
                null
        );
    }

    @Test
    void purchaseConfirmation_ShouldReturn204() throws Exception {
        UUID participanteId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        doNothing().when(service).purchaseConfirmation(any(PurchaseConfirmationRequest.class));

        var req = new PurchaseConfirmationRequest(participanteId, 1L, 10L);

        mvc.perform(post("/notifications/purchase-confirmation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isNoContent());
    }

    @Test
    void registrationConfirmation_ShouldReturn204() throws Exception {
        UUID participanteId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        doNothing().when(service).registrationConfirmation(any(RegistrationConfirmationRequest.class));

        var req = new RegistrationConfirmationRequest(participanteId);

        mvc.perform(post("/notifications/registration-confirmation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isNoContent());
    }

    @Test
    void eventReminder_ShouldReturn204() throws Exception {
        UUID participanteId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        doNothing().when(service).eventReminder(any(EventReminderRequest.class));

        var req = new EventReminderRequest(participanteId, 1L, null, null);

        mvc.perform(post("/notifications/event-reminder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isNoContent());
    }

    @Test
    void getById_ShouldReturn200_WithBody() throws Exception {
        Mockito.when(service.getById(5L)).thenReturn(sampleDto(5));

        mvc.perform(get("/notifications/find/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void list_ShouldReturn200_Paginated() throws Exception {
        Page<NotificationResponseDto> page =
                new PageImpl<>(List.of(sampleDto(1), sampleDto(2)), PageRequest.of(0, 2), 2);

        Mockito.when(service.list(0, 2, NotificationType.PURCHASE_CONFIRMATION))
                .thenReturn(page);

        mvc.perform(get("/notifications")
                        .param("page", "0")
                        .param("size", "2")
                        .param("type", "PURCHASE_CONFIRMATION"))
                .andExpect(status().isOk());
    }
}
