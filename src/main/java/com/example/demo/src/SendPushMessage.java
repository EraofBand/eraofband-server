package com.example.demo.src;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.JsonParseException;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

public class SendPushMessage {

    private ObjectMapper objectMapper;

    public SendPushMessage(ObjectMapper objectMapper){
        this.objectMapper=objectMapper;
    }

    public String makeMessage(String targetToken, String title, String body) throws JsonParseException, JsonProcessingException {
        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .token(targetToken)
                        .notification(FcmMessage.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(null)
                                .build()
                        ).build()).validateOnly(false).build();

        try {
            return objectMapper.writeValueAsString(fcmMessage);
        } catch (final JsonProcessingException e) {
            try {
                throw new Exception("Couldn't process object.", e);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase_service_key.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }
}
