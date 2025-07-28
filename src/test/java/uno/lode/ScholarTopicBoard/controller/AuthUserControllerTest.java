// AuthUserControllerTest.java
package uno.lode.ScholarTopicBoard.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.apache.catalina.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import uno.lode.ScholarTopicBoard.infra.security.AuthUser;
import uno.lode.ScholarTopicBoard.infra.security.SecurityFilter;
import uno.lode.ScholarTopicBoard.infra.security.AuthUserService;
import uno.lode.ScholarTopicBoard.infra.security.TokenService;
import uno.lode.ScholarTopicBoard.infra.exception.login.BadCredentialsUnauthorizedException;
import uno.lode.ScholarTopicBoard.infra.exception.login.RoleAccessDeniedException;
import uno.lode.ScholarTopicBoard.infra.exception.login.UserBannedException;

@WebMvcTest(
	    controllers = AuthUserController.class,
	    excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
	        SecurityFilter.class,
	        SecurityConfig.class
	    }),
	    excludeAutoConfiguration = {
	        SecurityAutoConfiguration.class,
	        UserDetailsServiceAutoConfiguration.class
	    }
	)

class AuthUserControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private AuthenticationManager authenticationManager;
    @MockBean private AuthUserService authUserService;
    @MockBean private TokenService tokenService;
    
    @Test
    @DisplayName("Should return 200 and token when login succeeds")
    void shouldLoginSuccessfully() throws Exception {
        var auth = mock(Authentication.class);
        var user = mock(AuthUser.class);

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(user);
        
        when(tokenService.createToken(user)).thenReturn("jwt-token");

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "user@example.com",
                      "password": "password"
                    }
                """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    @DisplayName("Should return 401 when email not found")
    void shouldReturnUnauthorizedWhenEmailNotFound() throws Exception {
        when(authenticationManager.authenticate(any()))
            .thenThrow(new BadCredentialsUnauthorizedException("User email not found in system"));

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "wrong@example.com",
                      "password": "123456"
                    }
                """))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 403 when user not enrolled")
    void shouldReturnForbiddenWhenUserNotEnrolled() throws Exception {
        var auth = mock(Authentication.class);
        var user = mock(AuthUser.class);

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(user);
        doThrow(new RoleAccessDeniedException("User not enrolled"))
            .when(authUserService).validateAccess(user);

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "user@example.com",
                      "password": "password"
                    }
                """))
            .andExpect(status().isForbidden());
    }
    
    @Test
    @DisplayName("Should return 403 when user is banned")
    void shouldReturnForbiddenWhenUserIsBanned() throws Exception {
        var auth = mock(Authentication.class);
        var user = mock(AuthUser.class);

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(user);
        doThrow(new UserBannedException("User banned"))
            .when(authUserService).validateAccess(user);

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "banned_user@example.com",
                      "password": "password"
                    }
                """))
            .andExpect(status().isForbidden());
    }
}
