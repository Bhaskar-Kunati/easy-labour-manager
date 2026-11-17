package com.easylabour.notificationservice.infrastructure.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;

@Service
@Slf4j
public class FcmClient {

    @Value("${firebase.credentials.path:#{null}}")
    private String credentialsPath;

    private boolean initialized = false;

    @PostConstruct
    public void init() {
        try {
            if (credentialsPath != null && !credentialsPath.isEmpty()) {
                FileInputStream serviceAccount = new FileInputStream(credentialsPath);
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                }
                initialized = true;
                log.info("Firebase Admin SDK initialized successfully.");
            } else {
                log.warn("Firebase credentials path not configured. FCM client running in local mock mode.");
            }
        } catch (IOException e) {
            log.warn("Failed to initialize Firebase Admin SDK: {}. Running in local mock mode.", e.getMessage());
        }
    }

    public void sendPushNotification(String token, String title, String body) {
        if (!initialized) {
            log.info("[MOCK FCM] Sending push to token {}: Title='{}', Body='{}'", token, title, body);
            return;
        }

        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM message sent successfully: {}", response);
        } catch (Exception e) {
            log.error("Failed to send FCM push notification to token {}: {}", token, e.getMessage());
        }
    }
}
