package org.dav.utils;

//import com.google.api.client.json.gson.GsonFactory;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
//import com.google.api.client.http.javanet.NetHttpTransport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dav.entity.User;
import org.dav.enums.Authorities;
import org.dav.exception.AuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleAuthService {

    private final String GOOGLE_CLIENT_ID;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GoogleAuthService(@Value("${google.client.id}") String googleClientId, HttpClient httpClient, ObjectMapper objectMapper) {
        GOOGLE_CLIENT_ID = googleClientId;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public User googleSignIn(String idToken){
        if (idToken == null || idToken.isEmpty()) {
            throw new AuthenticationException("Error in google sign in Try Again");
        }
        try {
            String GOOGLE_API = "https://www.googleapis.com/oauth2/v3/userinfo";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GOOGLE_API))
                    .header("Authorization", "Bearer " + idToken)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return extractUserFromPayload(response);
        } catch (InterruptedException | IOException e) {
            throw new AuthenticationException(e.getMessage());
        }
    }


    private User extractUserFromPayload(HttpResponse<String> response) throws JsonProcessingException {
        JsonNode userInfo = objectMapper.readTree(response.body());
        if(!userInfo.get("email_verified").asBoolean())
            throw new AuthenticationException("Unverified user, login with correct credentials or try again");
        return User.builder()
                .email(userInfo.get("email").asText())
                .firstname(userInfo.get("given_name").asText())
                .lastname(userInfo.get("family_name").asText())
                .profilePic(userInfo.get("picture").asText())
                .authorities(Authorities.member)
                .build();
    }
}
