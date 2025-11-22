// package com.webhook.service;

// import com.webhook.dto.PaymentRequest;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.mockito.Mock;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.client.RestClientException;
// import org.springframework.web.client.RestTemplate;

// import java.util.Map;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.*;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// class PaymentForwardServiceTest {

//     @Mock
//     RestTemplate restTemplate;

//     private PaymentService service;

//     @BeforeEach
//     void setUp() {
//         service = new PaymentService(restTemplate, "http://localhost/pay", 1);
//     }

//     @Test
//     void forwardSuccess() {
//         PaymentRequest req = new PaymentRequest();
//         req.setAmount(10.0);
//         req.setCurrency("BRL");

//         Map<String, String> body = Map.of("status", "OK");
//         ResponseEntity<Object> resp = ResponseEntity.ok(body);

//         when(restTemplate.postForEntity(anyString(), any(), eq(Object.class))).thenReturn(resp);

//         Object result = service.forwardPayment(req);

//         assertEquals(body, result);
//         verify(restTemplate, times(1)).postForEntity(anyString(), any(), eq(Object.class));
//     }

//     @Test
//     void forwardRetriesAndFails() {
//         PaymentRequest req = new PaymentRequest();
//         req.setAmount(10.0);
//         req.setCurrency("BRL");

//         when(restTemplate.postForEntity(anyString(), any(), eq(Object.class))).thenThrow(new RestClientException("down"));

//         assertThrows(RestClientException.class, () -> service.forwardPayment(req));
//         // initial attempt + 1 retry = 2
//         verify(restTemplate, times(2)).postForEntity(anyString(), any(), eq(Object.class));
//     }
// }
