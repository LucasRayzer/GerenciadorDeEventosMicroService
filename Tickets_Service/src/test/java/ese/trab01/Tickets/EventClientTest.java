package ese.trab01.Tickets;

import ese.trab01.Tickets.client.EventClient;
import ese.trab01.Tickets.client.EventClient.EventInfo;
import ese.trab01.Tickets.commons.StatusEvento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@org.junit.jupiter.api.extension.ExtendWith(MockitoExtension.class)
class EventClientTest {

    private static final String BASE_URL = "http://events-service";

    @Mock
    private RestTemplate restTemplate;

    private EventClient eventClient;

    @BeforeEach
    void setUp() {
        // usa o construtor normal
        eventClient = new EventClient(BASE_URL);
        // injeta o mock no campo final via reflection
        ReflectionTestUtils.setField(eventClient, "restTemplate", restTemplate);
    }

    @Test
    void getEventById_deveChamarApiCorretamenteERetornarEvento() {
        Long eventId = 42L;
        String expectedUrl = BASE_URL + "/eventos/" + eventId;

        EventInfo response = new EventInfo();
        response.setId(eventId);
        response.setNome("Evento Teste");
        response.setDescricao("Descricao");
        response.setLocalizacao("Local");
        response.setData(LocalDateTime.of(2025, 1, 1, 20, 0));
        response.setCapacidade(100);
        response.setVagas(50);
        response.setStatus(StatusEvento.ATIVO); // ajuste pro enum real que você tiver
        response.setOrganizerId(UUID.randomUUID());

        when(restTemplate.getForObject(expectedUrl, EventInfo.class)).thenReturn(response);

        EventInfo result = eventClient.getEventById(eventId);

        assertNotNull(result);
        assertEquals(eventId, result.getId());
        assertEquals("Evento Teste", result.getNome());
        assertEquals("Local", result.getLocalizacao());

        verify(restTemplate).getForObject(expectedUrl, EventInfo.class);
    }
}
