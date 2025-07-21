package com.appi147.expensetracker.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DummyController.class)
@Import(GlobalExceptionHandler.class)
@WithMockUser
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void handleResourceNotFound_returnsNotFound() throws Exception {
        mockMvc.perform(post("/dummy/not-found").with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

    @Test
    void handleUnauthorized_returnsUnauthorized() throws Exception {
        mockMvc.perform(post("/dummy/unauthorized").with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Unauthorized access"));
    }

    @Test
    void handleAccessDenied_returnsForbidden() throws Exception {
        mockMvc.perform(post("/dummy/access-denied").with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access denied"));
    }

    @Test
    void handleNoCredentials_returnsUnauthorized() throws Exception {
        mockMvc.perform(post("/dummy/no-credentials").with(csrf()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Authentication required"));
    }

    @Test
    void handleGenericException_returnsInternalServerError() throws Exception {
        mockMvc.perform(post("/dummy/generic-error").with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Something went wrong"));
    }
}
