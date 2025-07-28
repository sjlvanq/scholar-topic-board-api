// TokenServiceTest.java
package uno.lode.ScholarTopicBoard.infra.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setup() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", "my-secret-key");
    }

    @Test
    void shouldCreateAndVerifyToken() {
        AuthUser user = mock(AuthUser.class);
        when(user.getEmail()).thenReturn("user@example.com");

        String token = tokenService.createToken(user);
        String subject = tokenService.getSubject(token);

        assertEquals("user@example.com", subject);
    }

    @Test
    void shouldThrowOnInvalidToken() {
        assertThrows(RuntimeException.class, () -> tokenService.getSubject("invalid-token"));
    }
}
