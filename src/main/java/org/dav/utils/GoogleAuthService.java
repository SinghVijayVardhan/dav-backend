package org.dav.utils;

import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.dav.entity.User;
import org.dav.enums.Authorities;
import org.dav.exception.AuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleAuthService {

    private final String GOOGLE_CLIENT_ID;


    public GoogleAuthService(@Value("${google.client.id}") String googleClientId) {
        GOOGLE_CLIENT_ID = googleClientId;
    }

    public User googleSignIn(String idToken){
        if (idToken == null || idToken.isEmpty()) {
            throw new AuthenticationException("Error in google sign in Try Again");
        }
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                    .build();
            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken == null || !googleIdToken.getPayload().getEmailVerified()) {
                throw new AuthenticationException("Email verification failed. Try Again !!");
            }

            return extractUserFromPayload(googleIdToken.getPayload());
        } catch (GeneralSecurityException | IOException | IllegalArgumentException e) {
            throw new AuthenticationException(e.getMessage());
        }
    }


    private User extractUserFromPayload(GoogleIdToken.Payload payload){
        return User.builder()
                .email(payload.getEmail())
                .firstname((String) payload.get("given_name"))
                .lastname((String) payload.get("last_name"))
                .profilePic((String) payload.get("picture"))
                .authorities(Authorities.member)
                .build();
    }
}
