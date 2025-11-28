package com.paymentservice.service;

import com.paymentservice.client.TicketClient;
import com.paymentservice.model.Billing;
import com.paymentservice.model.enums.BillingStatus;
import com.paymentservice.repository.BillingRepository;
import com.paymentservice.repository.BillingTicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock
    private BillingRepository billingRepository;

    @Mock
    private BillingTicketRepository billingTicketRepository;

    @Mock
    private TicketClient ticketClient;

    @InjectMocks
    private BillingService billingService;

    @Test
    void createBillingPersistsExpectedValues() {
        when(billingRepository.save(any(Billing.class))).thenAnswer(invocation -> {
            Billing toSave = invocation.getArgument(0);
            toSave.setId(UUID.randomUUID());
            return toSave;
        });

        BigDecimal value = BigDecimal.valueOf(120.50);
        LocalDateTime dueDate = LocalDateTime.now().plusDays(2);

        Billing created = billingService.createBilling(value, "ticket-123", BillingStatus.PENDING, dueDate);

        ArgumentCaptor<Billing> captor = ArgumentCaptor.forClass(Billing.class);
        verify(billingRepository).save(captor.capture());
        Billing persisted = captor.getValue();

        assertThat(persisted.getValue()).isEqualByComparingTo(value);
        assertThat(persisted.getTicketId()).isEqualTo("ticket-123");
        assertThat(persisted.getStatus()).isEqualTo(BillingStatus.PENDING);
        assertThat(persisted.getDueDate()).isEqualTo(dueDate);
        assertThat(created.getId()).isNotNull();
    }

    @Test
    void updateBillingStatusUpdatesAndNotifiesTicketService() {
        UUID billingId = UUID.randomUUID();
        Billing existing = new Billing();
        existing.setId(billingId);
        existing.setTicketId("ticket-abc");
        existing.setStatus(BillingStatus.PENDING);

        when(billingRepository.findById(billingId)).thenReturn(Optional.of(existing));
        when(billingRepository.save(any(Billing.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Billing updated = billingService.updateBillingStatus(billingId, BillingStatus.PAID);

        assertThat(updated.getStatus()).isEqualTo(BillingStatus.PAID);
        verify(ticketClient).updateTicketStatus("ticket-abc", BillingStatus.PAID);
        verify(billingRepository).save(existing);
    }

    @Test
    void updateBillingStatusThrowsWhenNotFound() {
        UUID billingId = UUID.randomUUID();
        when(billingRepository.findById(billingId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                billingService.updateBillingStatus(billingId, BillingStatus.CANCELLED)
        );
        verifyNoInteractions(ticketClient);
    }

    @Test
    void listByTicketIdExpiresOverdueBillingsOnly() {
        LocalDateTime now = LocalDateTime.now();

        Billing overdue = new Billing();
        overdue.setId(UUID.randomUUID());
        overdue.setTicketId("ticket-1");
        overdue.setStatus(BillingStatus.PENDING);
        overdue.setDueDate(now.minusDays(1));

        Billing futurePending = new Billing();
        futurePending.setId(UUID.randomUUID());
        futurePending.setTicketId("ticket-1");
        futurePending.setStatus(BillingStatus.PENDING);
        futurePending.setDueDate(now.plusDays(1));

        Billing alreadyPaid = new Billing();
        alreadyPaid.setId(UUID.randomUUID());
        alreadyPaid.setTicketId("ticket-1");
        alreadyPaid.setStatus(BillingStatus.PAID);
        alreadyPaid.setDueDate(now.minusDays(2));

        when(billingTicketRepository.findByTicketId("ticket-1"))
                .thenReturn(List.of(overdue, futurePending, alreadyPaid));
        when(billingRepository.save(any(Billing.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Billing> result = billingService.listByTicketId("ticket-1");

        assertThat(result).hasSize(3);
        assertThat(overdue.getStatus()).isEqualTo(BillingStatus.EXPIRED);
        assertThat(futurePending.getStatus()).isEqualTo(BillingStatus.PENDING);
        assertThat(alreadyPaid.getStatus()).isEqualTo(BillingStatus.PAID);

        verify(billingRepository).save(overdue);
        verify(billingRepository, never()).save(futurePending);
        verify(billingRepository, never()).save(alreadyPaid);
    }
}
