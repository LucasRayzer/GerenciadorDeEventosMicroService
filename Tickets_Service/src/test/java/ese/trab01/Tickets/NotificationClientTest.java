package ese.trab01.Tickets;

import ese.trab01.Tickets.client.NotificationClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class NotificationClientTest {

    /**
     * Helper para criar o client com baseUrl e server mockado.
     */
    private NotificationClient createClientWithMockServer(String baseUrl,
                                                          MockRestServiceServer[] serverHolder) throws Exception {

        // instancia "crua" (sem Spring)
        NotificationClient client = new NotificationClient();

        // injeta o baseUrl via reflection
        Field baseUrlField = NotificationClient.class.getDeclaredField("baseUrl");
        baseUrlField.setAccessible(true);
        baseUrlField.set(client, baseUrl);

        // acessa o RestTemplate interno
        Field restField = NotificationClient.class.getDeclaredField("rest");
        restField.setAccessible(true);
        RestTemplate restTemplate = (RestTemplate) restField.get(client);

        // cria o MockRestServiceServer vinculado ao mesmo RestTemplate
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        serverHolder[0] = server;

        return client;
    }

    @Test
    @DisplayName("sendPurchaseConfirmation deve chamar endpoint correto com body esperado")
    void sendPurchaseConfirmation_shouldCallNotificationServiceWithCorrectBody() throws Exception {
        MockRestServiceServer[] holder = new MockRestServiceServer[1];
        String baseUrl = "http://notifications-service";
        NotificationClient client = createClientWithMockServer(baseUrl, holder);
        MockRestServiceServer server = holder[0];

        UUID participantId = UUID.randomUUID();
        Long eventId = 10L;
        Long ticketId = 20L;

        server.expect(requestTo(baseUrl + "/notifications/purchase-confirmation"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.participantId").value(participantId.toString()))
                .andExpect(jsonPath("$.eventId").value(eventId.intValue()))
                .andExpect(jsonPath("$.ticketId").value(ticketId.intValue()))
                .andRespond(withSuccess());

        client.sendPurchaseConfirmation(participantId, eventId, ticketId);

        server.verify();
    }

    @Test
    @DisplayName("registrationConfirmation deve chamar endpoint correto com participantId")
    void registrationConfirmation_shouldCallNotificationServiceWithParticipantId() throws Exception {
        MockRestServiceServer[] holder = new MockRestServiceServer[1];
        String baseUrl = "http://notifications-service";
        NotificationClient client = createClientWithMockServer(baseUrl, holder);
        MockRestServiceServer server = holder[0];

        UUID participantId = UUID.randomUUID();

        server.expect(requestTo(baseUrl + "/notifications/registration-confirmation"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.participantId").value(participantId.toString()))
                .andRespond(withSuccess());

        client.registrationConfirmation(participantId);

        server.verify();
    }

    @Test
    @DisplayName("sendTicketCanceled deve enviar body com reason quando informado")
    void sendTicketCanceled_shouldSendBodyWithReasonWhenProvided() throws Exception {
        MockRestServiceServer[] holder = new MockRestServiceServer[1];
        String baseUrl = "http://notifications-service";
        NotificationClient client = createClientWithMockServer(baseUrl, holder);
        MockRestServiceServer server = holder[0];

        UUID participantId = UUID.randomUUID();
        Long eventId = 10L;
        Long ticketId = 30L;
        String reason = "Duplicated purchase";

        server.expect(requestTo(baseUrl + "/notifications/ticket-canceled"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.participantId").value(participantId.toString()))
                .andExpect(jsonPath("$.eventId").value(eventId.intValue()))
                .andExpect(jsonPath("$.ticketId").value(ticketId.intValue()))
                .andExpect(jsonPath("$.reason").value(reason))
                .andRespond(withSuccess());

        client.sendTicketCanceled(participantId, eventId, ticketId, reason);

        server.verify();
    }

    @Test
    @DisplayName("sendTicketCanceled não deve incluir reason quando for null e não deve lançar exceção em erro")
    void sendTicketCanceled_shouldOmitReasonWhenNullAndNotThrowOnError() throws Exception {
        MockRestServiceServer[] holder = new MockRestServiceServer[1];
        String baseUrl = "http://notifications-service";
        NotificationClient client = createClientWithMockServer(baseUrl, holder);
        MockRestServiceServer server = holder[0];

        UUID participantId = UUID.randomUUID();
        Long eventId = 10L;
        Long ticketId = 30L;

        server.expect(requestTo(baseUrl + "/notifications/ticket-canceled"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.participantId").value(participantId.toString()))
                .andExpect(jsonPath("$.eventId").value(eventId.intValue()))
                .andExpect(jsonPath("$.ticketId").value(ticketId.intValue()))
                // aqui a gente só garante que reason NÃO aparece
                .andExpect(jsonPath("$.reason").doesNotExist())
                .andRespond(withServerError()); // força erro pra cair no catch

        // não deve estourar exceção por causa do try/catch no client
        assertThatCode(() ->
                client.sendTicketCanceled(participantId, eventId, ticketId, null)
        ).doesNotThrowAnyException();

        server.verify();
    }
}
