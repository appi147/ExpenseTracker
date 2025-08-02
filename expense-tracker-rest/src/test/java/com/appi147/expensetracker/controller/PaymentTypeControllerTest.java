package com.appi147.expensetracker.controller;

import com.appi147.expensetracker.entity.PaymentType;
import com.appi147.expensetracker.exception.GlobalExceptionHandler;
import com.appi147.expensetracker.service.PaymentTypeService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PaymentTypeController.class)
@Import({GlobalExceptionHandler.class, PaymentTypeControllerTest.TestConfig.class})
@WithMockUser
class PaymentTypeControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public PaymentTypeService paymentTypeService() {
            return Mockito.mock(PaymentTypeService.class);
        }
    }

    @Autowired
    private PaymentTypeService paymentTypeService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAll_shouldReturnList() throws Exception {
        when(paymentTypeService.getAll()).thenReturn(List.of(new PaymentType()));

        mockMvc.perform(get("/payment-types"))
                .andExpect(status().isOk());
    }

    @Test
    void create_shouldReturnCreated() throws Exception {
        PaymentType request = new PaymentType();
        request.setCode("CARD");
        request.setLabel("Card");

        when(paymentTypeService.create(any())).thenReturn(request);

        mockMvc.perform(post("/payment-types")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    { "code": "CARD", "label": "Card" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("CARD"));
    }

    @Test
    void update_shouldReturnUpdated() throws Exception {
        PaymentType updated = new PaymentType();
        updated.setCode("UPI");
        updated.setLabel("UPI");

        when(paymentTypeService.update(eq(1L), any())).thenReturn(updated);

        mockMvc.perform(put("/payment-types/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    { "code": "UPI", "label": "UPI" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("UPI"));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/payment-types/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(paymentTypeService).delete(1L);
    }
}

