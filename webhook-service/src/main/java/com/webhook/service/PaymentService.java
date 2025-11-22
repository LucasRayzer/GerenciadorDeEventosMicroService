package com.webhook.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Optional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webhook.dto.PaymentRequest;
import com.webhook.dto.PaymentResponse;
import com.webhook.model.PaymentHistory;
import com.webhook.repository.PaymentHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentHistoryRepository historyRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public PaymentService(PaymentHistoryRepository historyRepository, ObjectMapper objectMapper) {
        this.historyRepository = historyRepository;
        this.objectMapper = objectMapper;
    }

    public PaymentResponse savePayment(PaymentRequest request) {
        if (request.getAmount() == null || request.getCurrency() == null || request.getCurrency().isBlank()) {
            return new PaymentResponse(false, false, "amount and currency are required", null);
        }
        
        PaymentHistory history = new PaymentHistory();
        history.setEventId(request.getId());
        history.setAmount(request.getAmount());
        history.setCurrency(request.getCurrency());
        history.setPayerDocument(request.getPayer() != null ? request.getPayer().getDocument() : null);

        try {
            history.setRequestPayload(objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            history.setRequestPayload("{\"error\":\"payload-serialization-failed\"}");
        }

        history.setStatus("RECEIVED");
        historyRepository.save(history);

        return new PaymentResponse(true, false, "Payment queued for processing", null);
    }

    public Optional<PaymentHistory> getNextPayment() {
        return historyRepository.findOldestReceivedPayment();
    }

    public PaymentResponse updatePaymentStatus(Long id) {
        Optional<PaymentHistory> optionalHistory = historyRepository.findById(id);
        
        if (optionalHistory.isEmpty()) {
            return null;
        }
        
        PaymentHistory history = optionalHistory.get();
        history.setStatus("FORWARDED");
        historyRepository.save(history);
        
        return new PaymentResponse(true, true, 
            "Payment " + history.getEventId() + " status updated to FORWARDED");
    }
}
