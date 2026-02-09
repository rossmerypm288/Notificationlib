package com.notify.channel.push.provider;

import com.notify.channel.push.PushNotification;
import com.notify.config.ProviderConfig;
import com.notify.core.NotificationResult;
import com.notify.exception.SendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Proveedor de Push: Firebase Cloud Messaging.
 */
public class FirebaseProvider implements PushProvider {

    private static final Logger log = LoggerFactory.getLogger(FirebaseProvider.class);

    private final ProviderConfig config;

    public FirebaseProvider(ProviderConfig config) {
        config.getRequiredProperty("projectId");
        config.getRequiredProperty("serviceAccountKey");
        this.config = config;
    }

    @Override
    public NotificationResult send(PushNotification notification) {
        String projectId = config.getRequiredProperty("projectId");

        log.info("[Firebase FCM] Enviando push notification:");
        log.info("  Project: {}", projectId);
        log.info("  Token: {}...", notification.getRecipient().substring(0, Math.min(20, notification.getRecipient().length())));
        log.info("  Title: {}", notification.getTitle());
        log.info("  Body: {}", notification.getMessage());

        if (notification.getImageUrl() != null) {
            log.info("  Image: {}", notification.getImageUrl());
        }

        if (!notification.getData().isEmpty()) {
            log.info("  Data payload: {}", notification.getData().keySet());
        }

        try {
            // SIMULACIÓN
            // HTTP POST con OAuth2 Bearer token
            String messageId = "projects/" + projectId + "/messages/fcm-" +
                    UUID.randomUUID().toString().substring(0, 8);

            log.info("[Firebase FCM] Push enviado. MessageId: {}", messageId);
            return NotificationResult.success(notification.getId(), messageId);

        } catch (Exception e) {
            log.error("[Firebase FCM] Error: {}", e.getMessage());
            throw new SendException("Error en Firebase FCM: " + e.getMessage(), e);
        }
    }

    @Override
    public String getProviderName() {
        return "Firebase FCM";
    }
}
