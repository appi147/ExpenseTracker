package com.appi147.expensetracker.service;

import com.appi147.expensetracker.auth.GoogleTokenVerifier;
import com.appi147.expensetracker.auth.UserContext;
import com.appi147.expensetracker.entity.User;
import com.appi147.expensetracker.exception.UnauthorizedException;
import com.appi147.expensetracker.model.request.BudgetUpdate;
import com.appi147.expensetracker.model.request.ThemeUpdate;
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
        GoogleIdToken.Payload payload = googleTokenVerifier.verify(token);
        String sub = payload.getSubject();
        Optional<User> userOptional = userRepository.findById(sub);
        if (userOptional.isEmpty()) {
            throw new UnauthorizedException("Unauthorized");
        }
        User user = userOptional.get();
        return new LoginResponse(user);
    }

    public LoginResponse login(String token) throws GeneralSecurityException, IOException {
        GoogleIdToken.Payload payload = googleTokenVerifier.verify(token);

        // unique user id 255 chars
        String sub = payload.getSubject();

        // check if sub in db
        Optional<User> userOptional = userRepository.findById(sub);
        String name = (String) payload.get("name");
        String email = (String) payload.get("email");
        String firstName = (String) payload.get("given_name");
        String lastName = (String) payload.get("family_name");
        String pictureUrl = (String) payload.get("picture");
        User user;
        if (userOptional.isEmpty()) {
            user = new User();
            user.setUserId(sub);
        } else {
            user = userOptional.get();
        }
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setFullName(name);
        user.setEmail(email);
        user.setPictureUrl(pictureUrl);
        user.setLastLogin(ZonedDateTime.now(ZoneOffset.UTC));
        userRepository.saveAndFlush(user);

        return new LoginResponse(user);
    }

    public LoginResponse updateBudget(BudgetUpdate budgetUpdate) {
        User requester = UserContext.getCurrentUser();
        requester.setBudget(budgetUpdate.getAmount());
        return new LoginResponse(userRepository.saveAndFlush(requester));
    }

    public void updateTheme(ThemeUpdate themeUpdate) {
        User requester = UserContext.getCurrentUser();
        requester.setPreferredTheme(themeUpdate.getTheme());
        userRepository.saveAndFlush(requester);
    }
}
