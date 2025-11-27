package Auth.service;

import controller.AutenticacaoController;
import dto.AuthResponse;
import dto.LoginRequest;
import dto.RegisterRequest;
import model.TipoUsuario;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import repository.UserRepository;
import security.JwtUtil;
import service.ServiceAutenticacao;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticacaoControllerTest {

    @Mock
    private ServiceAutenticacao autenticacaoService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AutenticacaoController autenticacaoController;

    private final UUID userId = UUID.randomUUID();
    private final String username = "testuser@auth.com";
    private final String validToken = "valid.jwt.token";
    private User mockUser;
    private Date mockExpiration;

    @BeforeEach
    void setUp() {
        mockExpiration = new Date(System.currentTimeMillis() + 3600000);
        
        // Inicializa um usuário mock padrão
        mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername(username);
        mockUser.setTipo(TipoUsuario.CLIENTE);
        mockUser.setSenha("hashed_mock");
    }

    private LoginRequest criarLoginRequest(String username, String senha) {
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setSenha(senha);
        return request;
    }

    private RegisterRequest criarRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setNome("Nome Teste");
        request.setEmail(username);
        request.setSenha("senha123");
        request.setTipo(TipoUsuario.CLIENTE);
        return request;
    }

    // --- Testes de Login ---

    @Test
    void deveFazerLoginComSucesso() {
        LoginRequest request = criarLoginRequest(username, "senha123");
        AuthResponse authResponse = new AuthResponse(validToken, mockExpiration);

        when(autenticacaoService.login(username, "senha123")).thenReturn(authResponse);

        ResponseEntity<?> response = autenticacaoController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(authResponse);
        verify(autenticacaoService).login(username, "senha123");
    }

 
    @Test
    void deveRegistrarUsuarioComSucessoERetornarCreated() {
        RegisterRequest request = criarRegisterRequest();

        doNothing().when(autenticacaoService).register(request);

        ResponseEntity<?> response = autenticacaoController.register(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("Usuário cadastrado com sucesso!");
        verify(autenticacaoService).register(request);
    }
    
    // --- Testes de Validação ---

    @Test
    void deveValidarTokenComSucessoQuandoPassadoNoBody() {
        Map<String, String> body = Map.of("token", validToken);

        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.extractUsername(validToken)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(jwtUtil.extractExpiration(validToken)).thenReturn(mockExpiration);

        ResponseEntity<?> response = autenticacaoController.validateToken(body, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertThat(responseBody.get("username")).isEqualTo(username);
        assertThat(responseBody.get("userId")).isEqualTo(userId);
    }

    @Test
    void deveRetornarUnauthorizedQuandoTokenEstiverFaltando() {
        Map<String, String> body = Map.of(); // Body vazio
        
        ResponseEntity<?> response = autenticacaoController.validateToken(body, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertThat(responseBody.get("error")).isEqualTo("Missing token");
    }

    @Test
    void deveRetornarUnauthorizedParaTokenInvalido() {
        Map<String, String> body = Map.of("token", validToken);

        when(jwtUtil.validateToken(validToken)).thenReturn(false);

        ResponseEntity<?> response = autenticacaoController.validateToken(body, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertThat(responseBody.get("error")).isEqualTo("Invalid or expired token");
    }

    @Test
    void deveRetornarUnauthorizedSeUsuarioNaoForEncontrado() {
        Map<String, String> body = Map.of("token", validToken);

        when(jwtUtil.validateToken(validToken)).thenReturn(true);
        when(jwtUtil.extractUsername(validToken)).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty()); 

        ResponseEntity<?> response = autenticacaoController.validateToken(body, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertThat(responseBody.get("error")).isEqualTo("User not found");
    }
}