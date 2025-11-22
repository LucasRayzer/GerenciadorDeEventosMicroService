package com.webhook.repository;

import com.webhook.model.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    @Query("SELECT p FROM PaymentHistory p WHERE p.status = 'RECEIVED' ORDER BY p.createdAt ASC LIMIT 1")
    Optional<PaymentHistory> findOldestReceivedPayment();
}
