package com.appi147.expensetracker.exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dummy")
public class DummyController {

    @PostMapping("/not-found")
    public void throwNotFound() {
        throw new ResourceNotFoundException("Resource not found");
    }

    @PostMapping("/bad-request")
    public void throwBadRequest() {
        throw new BadRequestException("Bad request");
    }

    @PostMapping("/unauthorized")
    public void throwUnauthorized() {
        throw new UnauthorizedException("Unauthorized access");
    }

    @PostMapping("/forbidden")
    public void throwForbidden() {
        throw new ForbiddenException("Forbidden action");
    }

    @PostMapping("/access-denied")
    public void throwAccessDenied() {
        throw new AccessDeniedException("Access denied");
    }

    @PostMapping("/no-credentials")
    public void throwNoCredentials() {
        throw new AuthenticationCredentialsNotFoundException("No credentials");
    }

    @PostMapping("/generic-error")
    public void throwGeneric() throws Exception {
        throw new Exception("Generic error");
    }
}
