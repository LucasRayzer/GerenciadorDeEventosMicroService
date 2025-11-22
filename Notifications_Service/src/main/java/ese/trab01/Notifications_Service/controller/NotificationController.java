package ese.trab01.Notifications_Service.controller;

import ese.trab01.Notifications_Service.dto.*;
import ese.trab01.Notifications_Service.model.NotificationType;
import ese.trab01.Notifications_Service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @PostMapping("/purchase-confirmation")
    public ResponseEntity<Void> purchase(@RequestBody PurchaseConfirmationRequest req) {
        service.purchaseConfirmation(req);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/registration-confirmation")
    public ResponseEntity<Void> registration(@RequestBody RegistrationConfirmationRequest req) {
        service.registrationConfirmation(req);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/event-reminder")
    public ResponseEntity<Void> reminder(@RequestBody EventReminderRequest req) {
        service.eventReminder(req);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/ticket-canceled")
    public ResponseEntity<Void> ticketCanceled(@RequestBody TicketCanceledRequest req) {
        service.ticketCanceled(req);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/find/{id}")
    public ResponseEntity<NotificationResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<NotificationResponseDto>> list(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) NotificationType type
    ) {
        return ResponseEntity.ok(service.list(page, size, type));
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> getMinhasNotificacoes(
            Pageable pageable,
            @RequestHeader(value = "X-User-Id", required = false) UUID userId,
            @RequestHeader(value = "X-User-Roles", required = false) String rolesCsv
    ) {
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado.");

        return ResponseEntity.ok(service.buscarNotificacoesDoParticipante(userId, pageable));
    }
}
