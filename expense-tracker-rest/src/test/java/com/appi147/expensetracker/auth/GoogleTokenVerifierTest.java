package com.appi147.expensetracker.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class GoogleTokenVerifierTest {

    @Mock
    private GoogleIdTokenVerifier mockVerifier;

    @Mock
    private GoogleIdToken mockIdToken;

    @Mock
    private Payload mockPayload;

    private GoogleTokenVerifier googleTokenVerifier;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        googleTokenVerifier = new GoogleTokenVerifier("mock-client-id") {
            @Override
            protected GoogleIdTokenVerifier buildVerifier() {
                return mockVerifier;
            }
        };
    }

    @Test
    void shouldReturnPayloadWhenTokenIsValid() throws Exception {
        when(mockVerifier.verify("valid-token")).thenReturn(mockIdToken);
        when(mockIdToken.getPayload()).thenReturn(mockPayload);

        Payload result = googleTokenVerifier.verify("valid-token");

        assertThat(result).isEqualTo(mockPayload);
    }

    @Test
    void shouldThrowExceptionWhenTokenIsInvalid() throws Exception {
        when(mockVerifier.verify("invalid-token")).thenReturn(null);

        assertThatThrownBy(() -> googleTokenVerifier.verify("invalid-token"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid ID token.");
    }
}
