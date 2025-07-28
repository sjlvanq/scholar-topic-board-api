// AuthUserServiceTest.java
package uno.lode.ScholarTopicBoard.infra.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import uno.lode.ScholarTopicBoard.domain.user.User;
import uno.lode.ScholarTopicBoard.domain.user.UserRepository;
import uno.lode.ScholarTopicBoard.infra.exception.login.BadCredentialsUnauthorizedException;
import uno.lode.ScholarTopicBoard.infra.exception.login.RoleAccessDeniedException;
import uno.lode.ScholarTopicBoard.infra.exception.login.UserBannedException;

class AuthUserServiceTest {

    @Mock private UserRepository userRepository;
    private AuthUserService authUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authUserService = new AuthUserService();
        ReflectionTestUtils.setField(authUserService, "userRepository", userRepository);
    }

    @Test
    void shouldLoadUserByUsername() {
        var user = mock(User.class);
        when(userRepository.findByEmailWithRoles("test@mail.com")).thenReturn(Optional.of(user));

        var result = authUserService.loadUserByUsername("test@mail.com");

        assertTrue(result instanceof AuthUser);
    }

    @Test
    void shouldThrowWhenEmailNotFound() {
        when(userRepository.findByEmailWithRoles("missing@mail.com")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsUnauthorizedException.class,
            () -> authUserService.loadUserByUsername("missing@mail.com"));
    }

    @Test
    void shouldAllowAdminAccess() {
        var user = mock(AuthUser.class);
        when(user.isAdmin()).thenReturn(true);

        assertDoesNotThrow(() -> authUserService.validateAccess(user));
    }

    @Test
    void shouldDenyStudentWithoutCourses() {
        var user = mock(AuthUser.class);
        when(user.isAdmin()).thenReturn(false);
        when(user.isCoord()).thenReturn(false);
        when(user.hasCourses()).thenReturn(false);

        assertThrows(RoleAccessDeniedException.class, () -> authUserService.validateAccess(user));
    }
    
    @Test
    void shouldDenyBannedUsers() {
        var user = mock(AuthUser.class);
        when(user.isBanned()).thenReturn(true);

        assertThrows(UserBannedException.class, () -> authUserService.validateAccess(user));
    }
}
