package ese.trab01.Tickets;

import ese.trab01.Tickets.client.PaymentClient;
import ese.trab01.Tickets.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentClientTest {

    private static final String BASE_URL = "http://payment-service";

    @Mock
    private RestTemplate restTemplate;

    private PaymentClient paymentClient;

    @BeforeEach
    void setUp() {
        paymentClient = new PaymentClient();

        // injeta mock de RestTemplate e a baseUrl diretamente nos campos privados
        ReflectionTestUtils.setField(paymentClient, "rest", restTemplate);
        ReflectionTestUtils.setField(paymentClient, "baseUrl", BASE_URL);
    }

    @Test
    void createBilling_deveMontarUrlEBodyCorretamenteEChamarPost() {
        // ARRANGE: cria um Ticket real (sem Mockito)
        Ticket ticket = new Ticket();
        ticket.setId(10L);

        // cria um OffsetDateTime válido (com fuso horário)
        OffsetDateTime expiresAt = OffsetDateTime.of(
                2025, 1, 1,
                23, 59, 0, 0,
                ZoneOffset.UTC
        );
        ticket.setExpiresAt(expiresAt);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> bodyCaptor = ArgumentCaptor.forClass(Map.class);

        // ACT
        paymentClient.createBilling(ticket);

        // ASSERT
        verify(restTemplate).postForEntity(
                urlCaptor.capture(),
                bodyCaptor.capture(),
                eq(Void.class)
        );

        String capturedUrl = urlCaptor.getValue();
        Map<String, Object> capturedBody = bodyCaptor.getValue();

        assertEquals(BASE_URL + "/billings", capturedUrl);

        assertTrue(capturedBody.containsKey("ticketId"));
        assertTrue(capturedBody.containsKey("value"));
        assertTrue(capturedBody.containsKey("status"));
        assertTrue(capturedBody.containsKey("dueDate"));

        assertEquals(ticket.getId().toString(), capturedBody.get("ticketId"));
        assertEquals(10.00f, capturedBody.get("value")); // valor fixo do código
        assertEquals("PENDING", capturedBody.get("status"));
        // compara com o próprio toString do campo
        assertEquals(ticket.getExpiresAt().toString(), capturedBody.get("dueDate"));
    }
}
