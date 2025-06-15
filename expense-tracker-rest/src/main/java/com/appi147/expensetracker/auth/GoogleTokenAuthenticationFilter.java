package com.appi147.expensetracker.auth;

import com.appi147.expensetracker.model.response.LoginResponse;
import com.appi147.expensetracker.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class GoogleTokenAuthenticationFilter extends OncePerRequestFilter {

    private final UserService userService;

    public GoogleTokenAuthenticationFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                LoginResponse loginResponse = userService.getUserData(token);

                CustomUserDetails userDetails = new CustomUserDetails(loginResponse.getUser());

                Authentication auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception ex) {
                // Invalid token; optionally log or handle error
            }
        }

        filterChain.doFilter(request, response);
    }
}
