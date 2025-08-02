package com.appi147.expensetracker.auth;

import com.appi147.expensetracker.entity.User;
import com.appi147.expensetracker.model.response.LoginResponse;
import com.appi147.expensetracker.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GoogleTokenAuthenticationFilterTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private GoogleTokenAuthenticationFilter filter;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        filter = new GoogleTokenAuthenticationFilter(userService);
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldSetAuthenticationForValidToken() throws Exception {
        String token = "valid-token";
        String authHeader = "Bearer " + token;

        User mockUser = new User();
        mockUser.setUserId("userId");
        mockUser.setEmail("test@example.com");
        LoginResponse mockResponse = new LoginResponse(mockUser);

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(userService.getUserData(token)).thenReturn(mockResponse);

        filter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isInstanceOf(CustomUserDetails.class);
        assertThat(((CustomUserDetails) authentication.getPrincipal()).getUsername()).isEqualTo("userId");

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotSetAuthenticationForInvalidToken() throws Exception {
        String token = "invalid-token";
        String authHeader = "Bearer " + token;

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(userService.getUserData(token)).thenThrow(new RuntimeException("Invalid token"));

        filter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNull();

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldSkipIfNoAuthorizationHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldReturnTrueForExcludedPaths() {
        when(request.getRequestURI()).thenReturn("/api/user/login");
        assertThat(filter.shouldNotFilter(request)).isTrue();

        when(request.getRequestURI()).thenReturn("/api/actuator/health");
        assertThat(filter.shouldNotFilter(request)).isTrue();

        when(request.getRequestURI()).thenReturn("/api/swagger-ui/index.html");
        assertThat(filter.shouldNotFilter(request)).isTrue();

        when(request.getRequestURI()).thenReturn("/api/v3/api-docs");
        assertThat(filter.shouldNotFilter(request)).isTrue();
    }

    @Test
    void shouldReturnFalseForNonExcludedPaths() {
        when(request.getRequestURI()).thenReturn("/api/expense");
        assertThat(filter.shouldNotFilter(request)).isFalse();

        when(request.getRequestURI()).thenReturn("/some-other-path");
        assertThat(filter.shouldNotFilter(request)).isFalse();
    }
}

