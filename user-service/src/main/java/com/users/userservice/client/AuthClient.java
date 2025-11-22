package com.users.userservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public AuthClient(RestTemplateBuilder restTemplateBuilder,
                      @Value("${auth.service.base-url}") String baseUrl) {
        this.restTemplate = restTemplateBuilder.build();
        this.baseUrl = baseUrl;
    }

    public ResponseEntity<Void> register(String username, String password) {
        String url = baseUrl + "/auth/register";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RegisterRequest request = new RegisterRequest(username, password);
        HttpEntity<RegisterRequest> entity = new HttpEntity<>(request, headers);

        return restTemplate.postForEntity(url, entity, Void.class);
    }

    private record RegisterRequest(String username, String password) { }
}

