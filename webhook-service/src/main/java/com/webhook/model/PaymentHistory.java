package com.webhook.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "payment_history")
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventId;

    private Double amount;

    private String currency;

    private String payerDocument;


    private String status;


    private boolean forwarded;

    @Column(columnDefinition = "text")
    private String requestPayload;
    @Column(columnDefinition = "text")
    private String message;
    @Column(columnDefinition = "text")
    private String forwardedResponse;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    public PaymentHistory() {
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getPayerDocument() { return payerDocument; }
    public void setPayerDocument(String payerDocument) { this.payerDocument = payerDocument; }

    public String getRequestPayload() { return requestPayload; }
    public void setRequestPayload(String requestPayload) { this.requestPayload = requestPayload; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isForwarded() { return forwarded; }
    public void setForwarded(boolean forwarded) { this.forwarded = forwarded; }

    public String getForwardedResponse() { return forwardedResponse; }
    public void setForwardedResponse(String forwardedResponse) { this.forwardedResponse = forwardedResponse; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    @PrePersist
    public void prePersist() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
