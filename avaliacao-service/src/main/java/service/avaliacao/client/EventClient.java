package service.avaliacao.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import service.avaliacao.commons.StatusEvento;

import java.time.LocalDateTime;

@Component
public class EventClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    // Puxa a URL do application.properties
    public EventClient(@Value("${services.events.base-url}") String baseUrl) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl;
    }

    //busca os dados do evento
    public EventInfo getEventById(Long eventId) {
        String url = baseUrl + "/eventos/" + eventId;
        return restTemplate.getForObject(url, EventInfo.class);
    }


    /**
     * pegar os dados que vamos usar
     */
    @Getter
    @Setter
    public static class EventInfo {
        private Long id;
        private String nome;
        private StatusEvento status;
    }
}