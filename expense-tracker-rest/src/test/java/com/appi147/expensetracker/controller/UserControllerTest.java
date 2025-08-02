package com.appi147.expensetracker.controller;

import com.appi147.expensetracker.entity.User;
import com.appi147.expensetracker.enums.Role;
import com.appi147.expensetracker.enums.Theme;
import com.appi147.expensetracker.exception.GlobalExceptionHandler;
import com.appi147.expensetracker.model.response.LoginResponse;
import com.appi147.expensetracker.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@Import({GlobalExceptionHandler.class, UserControllerTest.TestConfig.class})
@WithMockUser
class UserControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }
    }

    @Autowired
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginWithGoogle_shouldReturnOk() throws Exception {
        User mockUser = new User();
        mockUser.setUserId("appi");
        mockUser.setEmail("test@example.com");
        mockUser.setFirstName("Test");
        mockUser.setLastName("User");
        mockUser.setPictureUrl("http://example.com/pic.jpg");
        mockUser.setRole(Role.USER);
        mockUser.setBudget(BigDecimal.valueOf(5000));
        mockUser.setPreferredTheme(Theme.LIGHT);
        LoginResponse response = new LoginResponse(mockUser);

        when(userService.updateBudget(any())).thenReturn(response);
        when(userService.login(any())).thenReturn(response);

        mockMvc.perform(post("/user/login")
                        .with(csrf())
                        .header("Authorization", "Bearer abc.def.ghi"))
                .andExpect(status().isOk());
    }

    @Test
    void loginWithGoogle_missingToken_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/user/login").with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithGoogle_invalidBearer_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/user/login")
                        .with(csrf())
                        .header("Authorization", "InvalidTokenFormat"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithGoogle_invalidToken_shouldReturnUnauthorized() throws Exception {
        when(userService.login(anyString())).thenThrow(new RuntimeException("Invalid token"));

        mockMvc.perform(post("/user/login")
                        .with(csrf())
                        .header("Authorization", "Bearer invalid.token"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid Google token"));
    }

    @Test
    void updateBudget_shouldReturnUpdated() throws Exception {
        User mockUser = new User();
        mockUser.setUserId("appi");
        mockUser.setEmail("test@example.com");
        mockUser.setFirstName("Test");
        mockUser.setLastName("User");
        mockUser.setPictureUrl("http://example.com/pic.jpg");
        mockUser.setRole(Role.USER);
        mockUser.setBudget(BigDecimal.valueOf(5000));
        mockUser.setPreferredTheme(Theme.LIGHT);
        LoginResponse response = new LoginResponse(mockUser);

        when(userService.updateBudget(any())).thenReturn(response);

        mockMvc.perform(put("/user/budget")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    { "amount": 5000.00 }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.budget").value(BigDecimal.valueOf(5000)));;
    }

    @Test
    void updateTheme_shouldReturnNoContent() throws Exception {
        mockMvc.perform(put("/user/theme")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    { "theme": "DARK" }
                                """))
                .andExpect(status().isNoContent());

        verify(userService).updateTheme(any());
    }
}

