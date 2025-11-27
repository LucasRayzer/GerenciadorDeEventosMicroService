package com.webhook.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webhook.dto.Payer;
import com.webhook.dto.PaymentRequest;
import com.webhook.dto.PaymentResponse;
import com.webhook.model.PaymentHistory;
import com.webhook.repository.PaymentHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    private PaymentHistoryRepository historyRepository;
    private ObjectMapper objectMapper;
    private PaymentService paymentService;

    @BeforeEach
    void setup() {
        historyRepository = mock(PaymentHistoryRepository.class);
        objectMapper = spy(new ObjectMapper());
        paymentService = new PaymentService(historyRepository, objectMapper);
    }

    @Test
    void savePayment_returnsBadRequestWhenAmountOrCurrencyMissing() {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(null);
        request.setCurrency("USD");

        PaymentResponse response = paymentService.savePayment(request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("amount and currency are required");
        verifyNoInteractions(historyRepository);
    }

    @Test
    void savePayment_persistsHistoryAndReturnsAccepted() {
        PaymentRequest request = new PaymentRequest(
                "evt-1", 10.5, "USD",
                new Payer("123", "John", "john@test.com"),
                Map.of("k", "v")
        );

        PaymentResponse response = paymentService.savePayment(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.isForwarded()).isFalse();
        assertThat(response.getMessage()).contains("queued");

        ArgumentCaptor<PaymentHistory> captor = ArgumentCaptor.forClass(PaymentHistory.class);
        verify(historyRepository).save(captor.capture());

        PaymentHistory saved = captor.getValue();
        assertThat(saved.getEventId()).isEqualTo("evt-1");
        assertThat(saved.getAmount()).isEqualTo(10.5);
        assertThat(saved.getCurrency()).isEqualTo("USD");
        assertThat(saved.getPayerDocument()).isEqualTo("123");
        assertThat(saved.getRequestPayload()).contains("\"id\":\"evt-1\"");
        assertThat(saved.getStatus()).isEqualTo("RECEIVED");
    }

    @Test
    void savePayment_handlesSerializationFailureGracefully() throws Exception {
        PaymentRequest request = new PaymentRequest("evt-err", 5.0, "USD", null, null);
        doThrow(JsonProcessingException.class).when(objectMapper).writeValueAsString(any());

        PaymentResponse response = paymentService.savePayment(request);

        assertThat(response.isSuccess()).isTrue();
        ArgumentCaptor<PaymentHistory> captor = ArgumentCaptor.forClass(PaymentHistory.class);
        verify(historyRepository).save(captor.capture());
        assertThat(captor.getValue().getRequestPayload()).contains("payload-serialization-failed");
    }

    @Test
    void getNextPayment_delegatesToRepository() {
        PaymentHistory history = new PaymentHistory();
        when(historyRepository.findOldestReceivedPayment()).thenReturn(Optional.of(history));

        Optional<PaymentHistory> result = paymentService.getNextPayment();

        assertThat(result).contains(history);
        verify(historyRepository).findOldestReceivedPayment();
    }

    @Test
    void updatePaymentStatus_returnsNullWhenNotFound() {
        when(historyRepository.findById(99L)).thenReturn(Optional.empty());

        PaymentResponse response = paymentService.updatePaymentStatus(99L);

        assertThat(response).isNull();
    }

    @Test
    void updatePaymentStatus_updatesStatusAndReturnsResponse() {
        PaymentHistory history = new PaymentHistory();
        history.setId(1L);
        history.setEventId("evt-2");
        history.setStatus("RECEIVED");
        when(historyRepository.findById(1L)).thenReturn(Optional.of(history));

        PaymentResponse response = paymentService.updatePaymentStatus(1L);

        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.isForwarded()).isTrue();
        assertThat(response.getMessage()).contains("FORWARDED");
        assertThat(history.getStatus()).isEqualTo("FORWARDED");
        verify(historyRepository, times(2)).save(any(PaymentHistory.class));
    }
}
