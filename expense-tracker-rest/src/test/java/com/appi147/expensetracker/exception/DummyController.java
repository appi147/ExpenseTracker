package com.appi147.expensetracker.exception;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dummy")
public class DummyController {

    @PostMapping
    public ResponseEntity<String> validate(@Valid @RequestBody DummyRequest request) {
        return ResponseEntity.ok("Valid");
    }

    @PostMapping("/not-found")
    public void notFound() {
        throw new ResourceNotFoundException("Resource not found");
    }

    @PostMapping("/unauthorized")
    public void unauthorized() {
        throw new UnauthorizedException("Unauthorized access");
    }

    @PostMapping("/access-denied")
    public void accessDenied() {
        throw new AccessDeniedException("Access denied");
    }

    @PostMapping("/no-credentials")
    public void noCredentials() {
        throw new AuthenticationCredentialsNotFoundException("Missing creds");
    }

    @PostMapping("/generic-error")
    public void genericError() {
        throw new RuntimeException("Unexpected error");
    }
}
