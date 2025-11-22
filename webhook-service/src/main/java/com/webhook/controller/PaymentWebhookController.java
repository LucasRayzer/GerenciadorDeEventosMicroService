package com.webhook.controller;

import com.webhook.dto.PaymentRequest;
import com.webhook.dto.PaymentResponse;
import com.webhook.model.PaymentHistory;
import com.webhook.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
public class PaymentWebhookController {

    private final PaymentService paymentService;
    //private final PaymentHistoryRepository historyRepository;

    public PaymentWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> receive(@RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.savePayment(request);
        
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/payment/next")
    public ResponseEntity<PaymentHistory> getNextPayment() {
        return paymentService.getNextPayment()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/payment/received/{id}")
    public ResponseEntity<PaymentResponse> updatePaymentStatus(@PathVariable Long id) {
        PaymentResponse response = paymentService.updatePaymentStatus(id);
        
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(response);
    }
}
