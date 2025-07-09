package com.admin.school.controllers.utils;

import com.admin.school.dto.user.UserRequestGoogleDTO;
import com.admin.school.exception.InvalidTokenException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

@Slf4j
public class GoogleTokenUtils {

    private static final String CLIENT_ID = "212249766947-23jmloduqtrcjob5iv9i9b1fko6jhc91.apps.googleusercontent.com";

    public static UserRequestGoogleDTO verifyAndGetUserRequestDTO(String rawToken) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();
        GoogleIdToken idToken;
        String token = rawToken.replace("\"", "");
        try {
            idToken = verifier.verify(token);
            GoogleIdToken.Payload payload = idToken.getPayload();

            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String picture = (String) payload.get("picture");
            UserRequestGoogleDTO userRequestDTO = new UserRequestGoogleDTO();
            userRequestDTO.setEmail(email);
            userRequestDTO.setName(name);
            userRequestDTO.setPicture(picture);
            return userRequestDTO;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
