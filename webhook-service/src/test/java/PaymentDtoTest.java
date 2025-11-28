
import org.junit.jupiter.api.Test;

import com.webhook.dto.Payer;
import com.webhook.dto.PaymentRequest;
import com.webhook.dto.PaymentResponse;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PaymentDtoTest {

    @Test
    void paymentResponse_isSuccessMirrorOfWebhookAccepted() {
        PaymentResponse response = new PaymentResponse(true, false, "ok");
        assertTrue(response.isSuccess());
        assertTrue(response.isWebhookAccepted());

        response.setWebhookAccepted(false);
        assertFalse(response.isSuccess());
        assertFalse(response.isWebhookAccepted());
    }

    @Test
    void paymentRequest_keepsAllFields() {
        Payer payer = new Payer("123", "John Doe", "john@example.com");
        Map<String, String> metadata = Map.of("orderId", "ABC");
        PaymentRequest request = new PaymentRequest("evt-1", 10.5, "USD", payer, metadata);

        assertEquals("evt-1", request.getId());
        assertEquals(10.5, request.getAmount());
        assertEquals("USD", request.getCurrency());
        assertSame(payer, request.getPayer());
        assertSame(metadata, request.getMetadata());
    }
}
