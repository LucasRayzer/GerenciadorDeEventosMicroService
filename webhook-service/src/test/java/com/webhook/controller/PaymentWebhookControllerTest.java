// package com.webhook.controller;

// import com.webhook.dto.PaymentRequest;
// import com.webhook.dto.PaymentResponse;
// import com.webhook.service.PaymentService;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.mockito.Mock;

// import java.util.Map;

// import org.springframework.http.ResponseEntity;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// class PaymentWebhookControllerTest {

//     @Mock
//     PaymentService forwardService;

//     @Mock
//     com.webhook.repository.PaymentHistoryRepository historyRepository;

//     private PaymentWebhookController controller;

//     @BeforeEach
//     void setUp() {
//         controller = new PaymentWebhookController(forwardService, historyRepository);
//     }

//     @Test
//     void receiveBadRequest() {
//         PaymentRequest req = new PaymentRequest();
//         req.setAmount(null);

//     ResponseEntity<PaymentResponse> resp = controller.receive(req);
//     assertEquals(org.springframework.http.HttpStatus.BAD_REQUEST, resp.getStatusCode());
//     assertFalse(resp.getBody().isForwarded());
//     }

//     @Test
//     void receiveForwarded() {
//         PaymentRequest req = new PaymentRequest();
//         req.setAmount(5.0);
//         req.setCurrency("BRL");

//         when(forwardService.forwardPayment(any())).thenReturn(Map.of("id", "pay_1"));

//         ResponseEntity<PaymentResponse> resp = controller.receive(req);

//         assertEquals(org.springframework.http.HttpStatus.OK, resp.getStatusCode());
//         assertTrue(resp.getBody().isForwarded());
//         assertEquals("Forwarded to payments service", resp.getBody().getMessage());
//         verify(historyRepository, atLeastOnce()).save(any());
//     }
// }
