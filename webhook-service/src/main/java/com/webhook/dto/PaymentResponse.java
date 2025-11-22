package com.webhook.dto;

public class PaymentResponse {
    private boolean webhookAccepted;
    private boolean forwarded;
    private String message;
    private Object forwardedResponse;

    public PaymentResponse() {
    }

    public PaymentResponse(boolean webhookAccepted, boolean forwarded, String message) {
        this.webhookAccepted = webhookAccepted;
        this.forwarded = forwarded;
        this.message = message;
    }

    public PaymentResponse(boolean webhookAccepted, boolean forwarded, String message, Object forwardedResponse) {
        this.webhookAccepted = webhookAccepted;
        this.forwarded = forwarded;
        this.message = message;
        this.forwardedResponse = forwardedResponse;
    }

    public boolean isWebhookAccepted() {
        return webhookAccepted;
    }

    public void setWebhookAccepted(boolean webhookAccepted) {
        this.webhookAccepted = webhookAccepted;
    }

    public boolean isSuccess() {
        return webhookAccepted;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isForwarded() {
        return forwarded;
    }

    public void setForwarded(boolean forwarded) {
        this.forwarded = forwarded;
    }

    public Object getForwardedResponse() {
        return forwardedResponse;
    }

    public void setForwardedResponse(Object forwardedResponse) {
        this.forwardedResponse = forwardedResponse;
    }

}
