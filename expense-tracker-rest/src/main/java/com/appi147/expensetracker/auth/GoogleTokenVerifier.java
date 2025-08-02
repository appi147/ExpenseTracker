package com.appi147.expensetracker.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GooglePublicKeysManager;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Component
public class GoogleTokenVerifier {

    private final String clientId;

    public GoogleTokenVerifier(@Value("${google.client.id}") String clientId) {
        this.clientId = clientId;
    }

    public GoogleIdToken.Payload verify(String idTokenString) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = buildVerifier();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            return idToken.getPayload();
        } else {
            throw new IllegalArgumentException("Invalid ID token.");
        }
    }

    protected GoogleIdTokenVerifier buildVerifier() throws GeneralSecurityException, IOException {
        return new GoogleIdTokenVerifier.Builder(
                new GooglePublicKeysManager.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance())
                        .build())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }
}

