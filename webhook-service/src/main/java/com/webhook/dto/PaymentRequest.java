package com.webhook.dto;

import java.util.Map;

public class PaymentRequest {
    private String id;
    private Double amount;
    private String currency;
    private Payer payer;
    private Map<String, String> metadata;

    public PaymentRequest() {
    }

    public PaymentRequest(String id, Double amount, String currency, Payer payer, Map<String, String> metadata) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.payer = payer;
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Payer getPayer() {
        return payer;
    }

    public void setPayer(Payer payer) {
        this.payer = payer;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
