package com.appi147.expensetracker.service;

import com.appi147.expensetracker.auth.UserContext;
import com.appi147.expensetracker.entity.PaymentType;
import com.appi147.expensetracker.entity.User;
import com.appi147.expensetracker.exception.ResourceNotFoundException;
import com.appi147.expensetracker.model.request.PaymentTypeRequest;
import com.appi147.expensetracker.repository.PaymentTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentTypeServiceTest {

    @Mock
    private PaymentTypeRepository paymentTypeRepository;

    @InjectMocks
    private PaymentTypeService paymentTypeService;

    // -- getByCode --

    @Test
    void getByCode_findsPaymentType_success() {
        PaymentType pt = new PaymentType();
        pt.setId(123L);
        pt.setCode("CARD");
        pt.setLabel("Credit Card");
        when(paymentTypeRepository.findByCode("CARD")).thenReturn(Optional.of(pt));

        PaymentType result = paymentTypeService.getByCode("CARD");

        assertNotNull(result);
        assertEquals("CARD", result.getCode());
        assertEquals("Credit Card", result.getLabel());
        assertEquals(123L, result.getId());
        verify(paymentTypeRepository).findByCode("CARD");
    }

    @Test
    void getByCode_notFound_throwsResourceNotFound() {
        when(paymentTypeRepository.findByCode("NOPE")).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> paymentTypeService.getByCode("NOPE"));
        assertTrue(e.getMessage().contains("NOPE"));
        verify(paymentTypeRepository).findByCode("NOPE");
    }

    @Test
    void getByCode_nullCode() {
        when(paymentTypeRepository.findByCode(null)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> paymentTypeService.getByCode(null));
        verify(paymentTypeRepository).findByCode(null);
    }

    // -- getAll --

    @Test
    void getAll_returnsList_ofPaymentTypes() {
        PaymentType pt1 = new PaymentType();
        pt1.setId(1L);
        pt1.setCode("CASH");
        PaymentType pt2 = new PaymentType();
        pt2.setId(2L);
        pt2.setCode("UPI");
        List<PaymentType> types = Arrays.asList(pt1, pt2);
        when(paymentTypeRepository.findAll()).thenReturn(types);

        List<PaymentType> result = paymentTypeService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("CASH", result.get(0).getCode());
        assertEquals("UPI", result.get(1).getCode());
        verify(paymentTypeRepository).findAll();
    }

    @Test
    void getAll_repositoryReturnsEmpty_thenReturnEmptyList() {
        when(paymentTypeRepository.findAll()).thenReturn(Collections.emptyList());

        List<PaymentType> result = paymentTypeService.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(paymentTypeRepository).findAll();
    }

    // -- create --

    @Test
    void create_success_savesAndReturnsNewPaymentType() {
        PaymentTypeRequest req = mock(PaymentTypeRequest.class);
        when(req.getCode()).thenReturn("WALLET");
        when(req.getLabel()).thenReturn("Wallet");

        PaymentType ptToPersist = new PaymentType();
        PaymentType ptSaved = new PaymentType();
        ptSaved.setId(55L);
        ptSaved.setCode("WALLET");
        ptSaved.setLabel("Wallet");

        User user = new User();
        user.setUserId("u999");
        try (MockedStatic<UserContext> uc = mockStatic(UserContext.class)) {
            uc.when(UserContext::getCurrentUser).thenReturn(user);

            ArgumentCaptor<PaymentType> captor = ArgumentCaptor.forClass(PaymentType.class);
            when(paymentTypeRepository.save(any(PaymentType.class))).thenReturn(ptSaved);

            PaymentType result = paymentTypeService.create(req);

            verify(req, atLeastOnce()).getCode();
            verify(req, atLeastOnce()).getLabel();
            verify(paymentTypeRepository).save(captor.capture());

            PaymentType saved = captor.getValue();
            assertEquals("WALLET", saved.getCode());
            assertEquals("Wallet", saved.getLabel());
            assertSame(user, saved.getCreatedBy());

            assertNotNull(result);
            assertEquals(55L, result.getId());
            assertEquals("WALLET", result.getCode());
            assertEquals("Wallet", result.getLabel());
        }
    }

    // -- update --

    @Test
    void update_successUpdatesAndReturnsType() {
        PaymentTypeRequest req = mock(PaymentTypeRequest.class);
        when(req.getCode()).thenReturn("NET");
        when(req.getLabel()).thenReturn("Internet Banking");

        PaymentType pt = new PaymentType();
        pt.setId(99L);
        pt.setCode("UPI");
        pt.setLabel("Old");

        PaymentType ptUpdated = new PaymentType();
        ptUpdated.setId(99L);
        ptUpdated.setCode("NET");
        ptUpdated.setLabel("Internet Banking");

        when(paymentTypeRepository.findById(99L)).thenReturn(Optional.of(pt));
        when(paymentTypeRepository.save(pt)).thenReturn(ptUpdated);

        PaymentType result = paymentTypeService.update(99L, req);

        verify(paymentTypeRepository).findById(99L);
        verify(paymentTypeRepository).save(pt);
        verify(req, atLeastOnce()).getCode();
        verify(req, atLeastOnce()).getLabel();

        // Confirm field updates
        assertEquals("NET", pt.getCode());
        assertEquals("Internet Banking", pt.getLabel());

        // The returned object is the saved one
        assertSame(ptUpdated, result);
        assertEquals("NET", result.getCode());
        assertEquals("Internet Banking", result.getLabel());
        assertEquals(99L, result.getId());
    }

    @Test
    void update_notFound_throwsResourceNotFoundException() {
        PaymentTypeRequest req = mock(PaymentTypeRequest.class);
        when(paymentTypeRepository.findById(777L)).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> paymentTypeService.update(777L, req));
        assertTrue(e.getMessage().contains("not found"));
        verify(paymentTypeRepository).findById(777L);
        verify(paymentTypeRepository, never()).save(any());
    }

    // -- delete --

    @Test
    void delete_invokesRepositoryDeleteById() {
        paymentTypeService.delete(50L);
        verify(paymentTypeRepository).deleteById(50L);
    }

    @Test
    void delete_negativeOrNullId_okayButStillCallsRepo() {
        paymentTypeService.delete(null);
        verify(paymentTypeRepository).deleteById(null);

        paymentTypeService.delete(-1L);
        verify(paymentTypeRepository).deleteById(-1L);
    }
}

