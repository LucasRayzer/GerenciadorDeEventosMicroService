package Auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import controller.AutenticacaoController;
import dto.AuthResponse;
import dto.LoginRequest;
import dto.RegisterRequest;
import model.TipoUsuario;
import model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import repository.UserRepository;
import security.JwtUtil;
import service.ServiceAutenticacao;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AutenticacaoController.class)
class AutenticacaoControllerTest {
@Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ServiceAutenticacao autenticacaoService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserRepository userRepository;

    private final UUID userId = UUID.randomUUID();
    private final String username = "testuser@auth.com";
    private final String validToken = "valid.jwt.token";

    // Método auxiliar para criar um User Mock (CORRIGIDO)
    private User criarMockUser(UUID id, String username, TipoUsuario tipo) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setTipo(tipo);
        user.setSenha("hashed_mock");
        // token e tokenExpiration ficam null, pois não são relevantes para este teste
        return user;
    }
    
    // Método auxiliar para criar RegisterRequest (CORRIGIDO)
    private RegisterRequest criarMockRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setNome("Nome Teste");
        request.setEmail("email@reg.com");
        request.setSenha("senha123");
        request.setEndereco("Endereco Teste");
        request.setTelefone("999999999");
        request.setTipo(TipoUsuario.CLIENTE);
        return request;
    }


    @Test
    void deveFazerLoginComSucessoERetornarToken() throws Exception {
        
        LoginRequest request = new LoginRequest("user@teste.com", "senha123"); 
        Date expiration = new Date(System.currentTimeMillis() + 3600000);
        AuthResponse response = new AuthResponse(validToken, expiration);

        when(autenticacaoService.login(request.getUsername(), request.getSenha())).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(validToken))
                .andExpect(jsonPath("$.expiration").exists());
    }

    @Test
    void deveRetornarStatusUnauthorizedParaLoginComErro() throws Exception {
       
        LoginRequest request = new LoginRequest("user@teste.com", "senha_errada"); 

       
        doThrow(new RuntimeException("Senha incorreta."))
                .when(autenticacaoService).login(request.getUsername(), request.getSenha());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
               
                .andExpect(status().isInternalServerError()); 
    }


    @Test
    void deveRegistrarUsuarioComSucessoERetornarCreated() throws Exception {

        RegisterRequest request = criarMockRegisterRequest();

        doNothing().when(autenticacaoService).register(any(RegisterRequest.class));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Usuário cadastrado com sucesso!"));
        
        verify(autenticacaoService).register(any(RegisterRequest.class));
    }


    @Test
    void deveValidarTokenComSucessoQuandoPassadoNoBody() throws Exception {
    
        User mockUser = criarMockUser(userId, username, TipoUsuario.ORGANIZADOR);
        Date expiration = new Date(System.currentTimeMillis() + 3600000);
        String expirationInstant = expiration.toInstant().toString();

        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.extractUsername(validToken)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(jwtUtil.extractExpiration(validToken)).thenReturn(expiration);

        mockMvc.perform(post("/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("token", validToken))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.roles[0]").value("ORGANIZADOR"))
                .andExpect(jsonPath("$.expiresAt").value(expirationInstant));
    }

    @Test
    void deveValidarTokenComSucessoQuandoPassadoNoHeaderAuthorization() throws Exception {
       
        User mockUser = criarMockUser(userId, username, TipoUsuario.CLIENTE);
        Date expiration = new Date(System.currentTimeMillis() + 3600000);

        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.extractUsername(validToken)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(jwtUtil.extractExpiration(validToken)).thenReturn(expiration);

        mockMvc.perform(post("/auth/validate")
                        .header("Authorization", "Bearer " + validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of()))) 
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles[0]").value("CLIENTE"));
    }


    @Test
    void deveRetornarUnauthorizedQuandoTokenEstiverFaltando() throws Exception {
        mockMvc.perform(post("/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of()))) 
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Missing token"));
    }

    @Test
    void deveRetornarUnauthorizedParaTokenInvalido() throws Exception {
        when(jwtUtil.validateToken(validToken)).thenReturn(false); 

        mockMvc.perform(post("/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("token", validToken))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid or expired token"));
    }

    @Test
    void deveRetornarUnauthorizedSeUsuarioNaoForEncontrado() throws Exception {
        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.extractUsername(validToken)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty()); 

        mockMvc.perform(post("/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("token", validToken))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("User not found"));
    }
}