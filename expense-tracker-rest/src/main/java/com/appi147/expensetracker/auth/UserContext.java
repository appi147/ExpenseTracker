package com.appi147.expensetracker.auth;

import com.appi147.expensetracker.entity.User;
import com.appi147.expensetracker.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserContext {

    private UserContext() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) auth.getPrincipal()).user();
        }
        throw new UnauthorizedException("User Unauthorized");
    }
}
