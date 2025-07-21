package com.appi147.expensetracker.controller;

import com.appi147.expensetracker.model.request.BudgetUpdate;
import com.appi147.expensetracker.model.request.ThemeUpdate;
import com.appi147.expensetracker.model.response.LoginResponse;
import com.appi147.expensetracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "User-related operations")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Login with Google", description = "Authenticates a user using Google OAuth token.")
    @PostMapping("/login")
    public ResponseEntity<?> loginWithGoogle(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }
        String token = authorizationHeader.substring(7); // strip "Bearer "

        try {
            return ResponseEntity.ok(userService.login(token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Google token");
        }
    }

    @Operation(summary = "Update monthly budget", description = "Updates the user's monthly budget.")
    @PutMapping("/budget")
    public ResponseEntity<LoginResponse> updateBudget(@Valid @RequestBody BudgetUpdate request) {
        return ResponseEntity.ok(userService.updateBudget(request));
    }

    @Operation(summary = "Update theme", description = "Updates the user's default theme.")
    @PutMapping("/theme")
    public ResponseEntity<Void> updateTheme(@Valid @RequestBody ThemeUpdate request) {
        userService.updateTheme(request);
        return ResponseEntity.noContent().build();
    }
}
