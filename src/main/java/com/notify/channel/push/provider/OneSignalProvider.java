package com.notify.channel.push.provider;

import com.notify.channel.push.PushNotification;
import com.notify.config.ProviderConfig;
import com.notify.core.NotificationResult;
import com.notify.exception.SendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Proveedor de Push: OneSignal .
 */
public class OneSignalProvider implements PushProvider {

    private static final Logger log = LoggerFactory.getLogger(OneSignalProvider.class);

    private final ProviderConfig config;

    public OneSignalProvider(ProviderConfig config) {
        config.getRequiredProperty("appId");
        config.getRequiredProperty("apiKey");
        this.config = config;
    }

    @Override
    public NotificationResult send(PushNotification notification) {
        String appId = config.getRequiredProperty("appId");

        log.info("[OneSignal] Enviando push notification:");
        log.info("  AppId: {}", appId);
        log.info("  Title: {}", notification.getTitle());
        log.info("  Body: {}", notification.getMessage());

        try {
            String messageId = "os-" + UUID.randomUUID().toString().substring(0, 12);

            log.info("[OneSignal] Push enviado. Id: {}", messageId);
            return NotificationResult.success(notification.getId(), messageId);

        } catch (Exception e) {
            log.error("[OneSignal] Error: {}", e.getMessage());
            throw new SendException("Error en OneSignal: " + e.getMessage(), e);
        }
    }

    @Override
    public String getProviderName() {
        return "OneSignal";
    }
}
