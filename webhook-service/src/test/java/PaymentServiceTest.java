
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webhook.dto.PaymentRequest;
import com.webhook.model.PaymentHistory;
import com.webhook.repository.PaymentHistoryRepository;
import com.webhook.dto.PaymentResponse;
import com.webhook.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentHistoryRepository historyRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void savePayment_deveRetornarErro_quandoAmountOuCurrencyForemInvalidos() {
        PaymentRequest request = mock(PaymentRequest.class);
        when(request.getAmount()).thenReturn(null);
        when(request.getCurrency()).thenReturn(null);

        PaymentResponse response = paymentService.savePayment(request);

        assertNotNull(response);
        assertFalse(response.isSuccess());              // ajuste se o nome do getter for diferente
        assertFalse(response.isForwarded());            // idem
        assertEquals("amount and currency are required", response.getMessage());

        verify(historyRepository, never()).save(any());
        verifyNoInteractions(objectMapper);
    }


    @Test
    void getNextPayment_deveDelegarParaRepositorio() {
        PaymentHistory history = new PaymentHistory();
        when(historyRepository.findOldestReceivedPayment())
                .thenReturn(Optional.of(history));

        Optional<PaymentHistory> result = paymentService.getNextPayment();

        assertTrue(result.isPresent());
        assertSame(history, result.get());
    }

    @Test
    void updatePaymentStatus_deveRetornarNull_quandoPagamentoNaoEncontrado() {
        when(historyRepository.findById(1L)).thenReturn(Optional.empty());

        PaymentResponse response = paymentService.updatePaymentStatus(1L);

        assertNull(response);
        verify(historyRepository, never()).save(any());
    }

    @Test
    void updatePaymentStatus_deveAtualizarStatusParaForwardedERetornarSucesso() {
        PaymentHistory history = new PaymentHistory();
        history.setId(1L);
        history.setEventId(String.valueOf(99L));
        history.setStatus("RECEIVED");

        when(historyRepository.findById(1L)).thenReturn(Optional.of(history));

        PaymentResponse response = paymentService.updatePaymentStatus(1L);

        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertTrue(response.isForwarded());
        assertEquals("FORWARDED", history.getStatus());
        assertEquals(
                "Payment " + history.getEventId() + " status updated to FORWARDED",
                response.getMessage()
        );

        verify(historyRepository).save(history);
    }
}
