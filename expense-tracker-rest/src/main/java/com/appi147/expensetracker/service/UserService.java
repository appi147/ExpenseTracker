package com.appi147.expensetracker.service;

import com.appi147.expensetracker.auth.GoogleTokenVerifier;
import com.appi147.expensetracker.auth.UserContext;
import com.appi147.expensetracker.entity.User;
import com.appi147.expensetracker.exception.UnauthorizedException;
import com.appi147.expensetracker.model.request.BudgetUpdateRequest;
import com.appi147.expensetracker.model.request.ThemeUpdateRequest;
import com.appi147.expensetracker.model.response.LoginResponse;
import com.appi147.expensetracker.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final GoogleTokenVerifier googleTokenVerifier;
    private final UserRepository userRepository;

    public LoginResponse getUserData(String token) throws GeneralSecurityException, IOException {
        log.info("[UserService] Fetching user data with Google token");
        GoogleIdToken.Payload payload = googleTokenVerifier.verify(token);
        String sub = payload.getSubject();
        Optional<User> userOptional = userRepository.findById(sub);
        if (userOptional.isEmpty()) {
            log.warn("[UserService] No user found with Google sub: {}", sub);
            throw new UnauthorizedException("Unauthorized");
        }
        User user = userOptional.get();
        log.info("[UserService] User data found: userId={}, email={}", user.getUserId(), user.getEmail());
        return new LoginResponse(user);
    }

    public LoginResponse login(String token) throws GeneralSecurityException, IOException {
        log.info("[UserService] Login attempt with Google token");
        GoogleIdToken.Payload payload = googleTokenVerifier.verify(token);

        String sub = payload.getSubject();

        Optional<User> userOptional = userRepository.findById(sub);
        String name = (String) payload.get("name");
        String email = (String) payload.get("email");
        String firstName = (String) payload.get("given_name");
        String lastName = (String) payload.get("family_name");
        String pictureUrl = (String) payload.get("picture");
        User user;
        final boolean isNewUser;
        if (userOptional.isEmpty()) {
            user = new User();
            user.setUserId(sub);
            isNewUser = true;
        } else {
            user = userOptional.get();
            isNewUser = false;
        }
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setFullName(name);
        user.setEmail(email);
        user.setPictureUrl(pictureUrl);
        user.setLastLogin(ZonedDateTime.now(ZoneOffset.UTC));
        userRepository.saveAndFlush(user);

        log.info("[UserService] User {}: userId={}, email={}, name={}",
                isNewUser ? "registered" : "login", user.getUserId(), user.getEmail(), user.getFullName());
        return new LoginResponse(user);
    }

    public LoginResponse updateBudget(BudgetUpdateRequest budgetUpdateRequest) {
        User requester = UserContext.getCurrentUser();
        log.info("[UserService] User [{}] updating budget to {}", requester.getUserId(), budgetUpdateRequest.getAmount());
        requester.setBudget(budgetUpdateRequest.getAmount());
        LoginResponse response = new LoginResponse(userRepository.saveAndFlush(requester));
        log.info("[UserService] Budget updated for user [{}]", requester.getUserId());
        return response;
    }

    public void updateTheme(ThemeUpdateRequest themeUpdateRequest) {
        User requester = UserContext.getCurrentUser();
        log.info("[UserService] User [{}] updating preferred theme to '{}'", requester.getUserId(), themeUpdateRequest.getTheme());
        requester.setPreferredTheme(themeUpdateRequest.getTheme());
        userRepository.saveAndFlush(requester);
        log.info("[UserService] Preferred theme updated for user [{}]", requester.getUserId());
    }
}
