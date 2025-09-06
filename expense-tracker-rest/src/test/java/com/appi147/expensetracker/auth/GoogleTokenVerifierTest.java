package com.appi147.expensetracker.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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
        // Subclass to override protected method
        googleTokenVerifier = new GoogleTokenVerifier("mock-client-id") {
            @Override
            protected GoogleIdTokenVerifier buildVerifier() {
                return mockVerifier;
            }
        };
    }

    @Test
    void verify_validToken_returnsPayload() throws Exception {
        // Arrange
        when(mockVerifier.verify("valid-token")).thenReturn(mockIdToken);
        when(mockIdToken.getPayload()).thenReturn(mockPayload);

        // Act
        Payload result = googleTokenVerifier.verify("valid-token");

        // Assert
        assertThat(result).isEqualTo(mockPayload);
    }

    @Test
    void verify_invalidToken_throwsIllegalArgumentException() throws Exception {
        // Arrange
        when(mockVerifier.verify("invalid-token")).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> googleTokenVerifier.verify("invalid-token"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid ID token.");
    }

    private static class TestVerifier extends GoogleTokenVerifier {
        public TestVerifier(String clientId) {
            super(clientId);
        }

        public GoogleIdTokenVerifier callBuildVerifier() throws GeneralSecurityException, IOException {
            return buildVerifier();
        }
    }

    @Test
    void buildVerifier_shouldReturnVerifierInstance() throws Exception {
        TestVerifier verifier = new TestVerifier("mock-client-id");

        GoogleIdTokenVerifier result = verifier.callBuildVerifier();

        assertThat(result).isNotNull();
    }
}
