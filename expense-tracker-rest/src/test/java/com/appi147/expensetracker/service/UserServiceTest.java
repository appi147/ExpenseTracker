package com.appi147.expensetracker.service;

import com.appi147.expensetracker.auth.GoogleTokenVerifier;
import com.appi147.expensetracker.auth.UserContext;
import com.appi147.expensetracker.entity.User;
import com.appi147.expensetracker.enums.Theme;
import com.appi147.expensetracker.exception.UnauthorizedException;
import com.appi147.expensetracker.model.request.BudgetUpdateRequest;
import com.appi147.expensetracker.model.request.ThemeUpdateRequest;
import com.appi147.expensetracker.model.response.LoginResponse;
import com.appi147.expensetracker.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    GoogleTokenVerifier googleTokenVerifier;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Mock
    GoogleIdToken.Payload payload;

    // getUserData: Valid user path
    @Test
    void getUserData_ValidUser_ReturnsFullUserData() throws Exception {
        String token = "valid";
        String subjectId = "userGoogleSub";
        User testUser = new User();
        testUser.setUserId(subjectId);
        testUser.setEmail("test@user.com");
        // other fields can be set if relevant to getUserData()

        when(googleTokenVerifier.verify(token)).thenReturn(payload);
        when(payload.getSubject()).thenReturn(subjectId);
        when(userRepository.findById(subjectId)).thenReturn(Optional.of(testUser));

        LoginResponse response = userService.getUserData(token);

        assertNotNull(response, "LoginResponse should not be null");
        assertNotNull(response.getUser(), "User in response should not be null");
        assertEquals(subjectId, response.getUser().getUserId());
        assertEquals("test@user.com", response.getUser().getEmail());
    }

    // getUserData: User does not exist
    @Test
    void getUserData_WhenUserNotFound_ShouldThrowUnauthorized() throws Exception {
        String token = "token";
        String subjectId = "sub";

        when(googleTokenVerifier.verify(token)).thenReturn(payload);
        when(payload.getSubject()).thenReturn(subjectId);
        when(userRepository.findById(subjectId)).thenReturn(Optional.empty());

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class, () -> userService.getUserData(token));
        assertEquals("Unauthorized", exception.getMessage());
    }

    // getUserData: Verification fails (invalid token/GS exception)
    @Test
    void getUserData_WhenGoogleTokenFails_ShouldPropagateException() throws Exception {
        String token = "token";
        when(googleTokenVerifier.verify(token)).thenThrow(new GeneralSecurityException("Bad token"));

        GeneralSecurityException ex = assertThrows(GeneralSecurityException.class, () ->
                userService.getUserData(token));
        assertTrue(ex.getMessage().contains("Bad token"));
    }

    // login: Existing user, should update relevant user fields and persist
    @Test
    void login_ExistingUser_ShouldUpdateUser() throws Exception {
        String token = "tok";
        String sub = "subExist";
        User user = new User();
        user.setUserId(sub);
        user.setFirstName("Old");
        user.setLastName("User");
        user.setEmail("old@email.com");
        user.setFullName("Old User");
        user.setPictureUrl("oldpic.jpg");

        when(googleTokenVerifier.verify(token)).thenReturn(payload);
        when(payload.getSubject()).thenReturn(sub);
        when(payload.get("name")).thenReturn("Jane Smith");
        when(payload.get("email")).thenReturn("jane@smith.com");
        when(payload.get("given_name")).thenReturn("Jane");
        when(payload.get("family_name")).thenReturn("Smith");
        when(payload.get("picture")).thenReturn("pic.jpg");
        when(userRepository.findById(sub)).thenReturn(Optional.of(user));
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        ZonedDateTime before = ZonedDateTime.now();

        LoginResponse response = userService.login(token);

        // All fields must be updated correctly
        assertNotNull(response);
        User actual = response.getUser();
        assertEquals(sub, actual.getUserId());
        assertEquals("Jane", actual.getFirstName());
        assertEquals("Smith", actual.getLastName());
        assertEquals("Jane Smith", actual.getFullName());
        assertEquals("jane@smith.com", actual.getEmail());
        assertEquals("pic.jpg", actual.getPictureUrl());
        assertNotNull(actual.getLastLogin());
        assertTrue(!actual.getLastLogin().isBefore(before),
                "lastLogin should be updated to a time after method invocation");
    }

    // login: New user, all fields must be set, saved, and returned
    @Test
    void login_NewUser_ShouldCreateUserWithAllFields() throws Exception {
        String token = "tok";
        String sub = "newsub";
        when(googleTokenVerifier.verify(token)).thenReturn(payload);
        when(payload.getSubject()).thenReturn(sub);
        when(payload.get("name")).thenReturn("Mary Neo");
        when(payload.get("email")).thenReturn("mary@neo.com");
        when(payload.get("given_name")).thenReturn("Mary");
        when(payload.get("family_name")).thenReturn("Neo");
        when(payload.get("picture")).thenReturn("avatar.jpg");
        when(userRepository.findById(sub)).thenReturn(Optional.empty());
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        ZonedDateTime before = ZonedDateTime.now();

        LoginResponse response = userService.login(token);

        assertNotNull(response, "Response cannot be null for new user");
        User user = response.getUser();
        assertEquals(sub, user.getUserId());
        assertEquals("Mary", user.getFirstName());
        assertEquals("Neo", user.getLastName());
        assertEquals("Mary Neo", user.getFullName());
        assertEquals("mary@neo.com", user.getEmail());
        assertEquals("avatar.jpg", user.getPictureUrl());
        assertNotNull(user.getLastLogin());
        assertTrue(!user.getLastLogin().isBefore(before));
    }

    // login: Token verification fails (IOException)
    @Test
    void login_WhenTokenVerificationFails_ShouldPropagateException() throws Exception {
        String token = "fail";
        when(googleTokenVerifier.verify(token)).thenThrow(new IOException("Net issue"));

        IOException ex = assertThrows(IOException.class, () -> userService.login(token));
        assertTrue(ex.getMessage().contains("Net issue"));
    }

    // updateBudget: User present and save successful, update reflected in response
    @Test
    void updateBudget_ValidUser_ShouldUpdateBudget() {
        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            User user = new User();
            user.setUserId("u123");
            user.setBudget(BigDecimal.TEN);

            uc.when(UserContext::getCurrentUser).thenReturn(user);

            BudgetUpdateRequest upd = new BudgetUpdateRequest();
            upd.setAmount(BigDecimal.valueOf(5000));

            when(userRepository.saveAndFlush(user)).thenReturn(user);

            LoginResponse resp = userService.updateBudget(upd);

            assertNotNull(resp);
            assertEquals(user, resp.getUser());
            assertEquals(BigDecimal.valueOf(5000), user.getBudget());
            verify(userRepository, times(1)).saveAndFlush(user);
        }
    }

    // updateTheme: Properly updates user's preferred theme
    @Test
    void updateTheme_ShouldUpdatePreferredTheme() {
        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            User user = new User();
            user.setUserId("themeUser");
            user.setPreferredTheme(Theme.LIGHT);

            uc.when(UserContext::getCurrentUser).thenReturn(user);

            ThemeUpdateRequest upd = new ThemeUpdateRequest();
            upd.setTheme(Theme.DARK);

            when(userRepository.saveAndFlush(user)).thenReturn(user);

            userService.updateTheme(upd);

            assertEquals(Theme.DARK, user.getPreferredTheme());
            verify(userRepository, times(1)).saveAndFlush(user);
        }
    }

    // updateBudget: UserContext returns null user - must throw NullPointerException
    @Test
    void updateBudget_NoCurrentUser_ShouldThrowNPE() {
        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(null);

            BudgetUpdateRequest upd = new BudgetUpdateRequest();
            upd.setAmount(BigDecimal.valueOf(100));

            NullPointerException ex = assertThrows(NullPointerException.class, () ->
                    userService.updateBudget(upd));
            assertTrue(ex.getMessage() == null || ex.getMessage().isEmpty() || ex.getMessage().contains("null"),
                    "Expected NPE message for null current user");
        }
    }

    // updateTheme: UserContext returns null user - must throw NullPointerException
    @Test
    void updateTheme_NoCurrentUser_ShouldThrowNPE() {
        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(null);

            ThemeUpdateRequest upd = new ThemeUpdateRequest();
            upd.setTheme(Theme.DARK);

            NullPointerException ex = assertThrows(NullPointerException.class, () ->
                    userService.updateTheme(upd));
            assertTrue(ex.getMessage() == null || ex.getMessage().isEmpty() || ex.getMessage().contains("null"),
                    "Expected NPE message for null current user");
        }
    }
}
