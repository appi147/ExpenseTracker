package com.appi147.expensetracker.auth;

import com.appi147.expensetracker.entity.User;
import com.appi147.expensetracker.exception.UnauthorizedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserContextTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnCurrentUserWhenAuthenticated() {
        User mockUser = new User();
        mockUser.setEmail("test@example.com");

        CustomUserDetails userDetails = new CustomUserDetails(mockUser);
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);

        User currentUser = UserContext.getCurrentUser();

        assertThat(currentUser).isEqualTo(mockUser);
    }

    @Test
    void shouldThrowExceptionWhenAuthenticationIsNull() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(UserContext::getCurrentUser)
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("User Unauthorized");
    }

    @Test
    void shouldThrowExceptionWhenPrincipalIsNotCustomUserDetails() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("someStringPrincipal", null, null);

        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThatThrownBy(UserContext::getCurrentUser)
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("User Unauthorized");
    }
}
