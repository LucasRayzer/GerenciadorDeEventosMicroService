package ese.trab01.Notifications_Service.service;

import ese.trab01.Notifications_Service.dto.*;
import ese.trab01.Notifications_Service.exception.RecursoNaoEncontradoException;
import ese.trab01.Notifications_Service.model.*;
import ese.trab01.Notifications_Service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository repository;

    private NotificationResponseDto toDto(Notification n) {
        return new NotificationResponseDto(
                n.getId(), n.getType(), n.getChannel(), n.getStatus(),
                n.getParticipantId(), n.getEventId(), n.getTicketId(),
                n.getRecipient(), n.getSubject(), n.getMessage(),
                n.getCreatedAt(), n.getSentAt()
        );
    }

    @Transactional
    public void purchaseConfirmation(PurchaseConfirmationRequest req) {
        Notification n = Notification.builder()
                .type(NotificationType.PURCHASE_CONFIRMATION)
                .channel(NotificationChannel.IN_APP)    // mock: não enviamos nada, só persistimos
                .participantId(req.participantId())
                .eventId(req.eventId())
                .ticketId(req.ticketId())
                .status(NotificationStatus.PENDING)
                .createdAt(OffsetDateTime.now())
                .subject("Compra confirmada")
                .message("Ticket " + req.ticketId() + " confirmado para o evento " + req.eventId())
                .metadata(null)
                .build();
        repository.save(n);
        log.info("[mock] Notificação de compra registrada: {}", n.getId());
    }

    @Transactional
    public void registrationConfirmation(RegistrationConfirmationRequest req) {
        Notification n = Notification.builder()
                .type(NotificationType.REGISTRATION_CONFIRMATION)
                .channel(NotificationChannel.IN_APP)
                .participantId(req.participantId())
                .status(NotificationStatus.PENDING)
                .createdAt(OffsetDateTime.now())
                .subject("Inscrição confirmada")
                .message("Inscrição confirmada para participante " + req.participantId())
                .build();
        repository.save(n);
        log.info("[mock] Notificação de registro registrada: {}", n.getId());
    }

    @Transactional
    public void eventReminder(EventReminderRequest req) {
        String msg = "Lembrete do evento " + (req.eventName() != null ? req.eventName() : req.eventId())
                + " em " + req.eventDateTime();
        Notification n = Notification.builder()
                .type(NotificationType.EVENT_REMINDER)
                .channel(NotificationChannel.IN_APP)
                .participantId(req.participantId())
                .eventId(req.eventId())
                .status(NotificationStatus.PENDING)
                .createdAt(OffsetDateTime.now())
                .subject("Lembrete de evento")
                .message(msg)
                .build();
        repository.save(n);
        log.info("[mock] Notificação de lembrete registrada: {}", n.getId());
    }

    @Transactional(readOnly = true)
    public NotificationResponseDto getById(Long id) {
        Notification n = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Notificação não encontrada"));
        return toDto(n);
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponseDto> list(Integer page, Integer size, NotificationType type) {
        Pageable p = PageRequest.of(page != null ? page : 0, size != null ? size : 10, Sort.by("id").descending());
        Page<Notification> data = (type == null)
                ? repository.findAll(p)
                : repository.findByType(type, p);
        return data.map(this::toDto);
    }

    @Transactional
    public void ticketCanceled(TicketCanceledRequest req) {
        Notification n = Notification.builder()
                .type(NotificationType.TICKET_CANCELED)
                .channel(NotificationChannel.IN_APP)
                .participantId(req.participantId())
                .eventId(req.eventId())
                .ticketId(req.ticketId())
                .status(NotificationStatus.PENDING)
                .subject("Seu ingresso foi cancelado")
                .message("Ticket %d para evento %d foi cancelado. %s"
                        .formatted(req.ticketId(), req.eventId(),
                                req.reason() != null ? "Motivo: " + req.reason() : ""))
                .createdAt(OffsetDateTime.now())
                .build();
        repository.save(n);
        log.info("[mock] Notificação de cancelamento registrada: {}", n.getId());
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Page<NotificationResponseDto> buscarNotificacoesDoParticipante(UUID organizerId, Pageable pageable) {
        return repository.findByParticipantId(organizerId, pageable).map(this::paraRespostaDto);
    }

    private NotificationResponseDto paraRespostaDto(ese.trab01.Notifications_Service.model.Notification n) {
        return new NotificationResponseDto(
                n.getId(),
                n.getType(),
                n.getChannel(),
                n.getStatus(),
                n.getParticipantId(),
                n.getEventId(),
                n.getTicketId(),
                n.getRecipient(),
                n.getSubject(),
                n.getMessage(),
                n.getCreatedAt(),
                n.getSentAt()
        );
    }


}
