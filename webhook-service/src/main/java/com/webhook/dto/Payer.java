package com.webhook.dto;

public class Payer {
    private String document;
    private String name;
    private String email;

    public Payer() {
    }

    public Payer(String document, String name, String email) {
        this.document = document;
        this.name = name;
        this.email = email;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
