package com.notify.channel.email.provider;

import com.notify.channel.email.EmailNotification;
import com.notify.config.ProviderConfig;
import com.notify.core.NotificationResult;
import com.notify.exception.SendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Proveedor de email: SendGrid (simulado).
 */
public class SendGridProvider implements EmailProvider {

    private static final Logger log = LoggerFactory.getLogger(SendGridProvider.class);

    private final ProviderConfig config;

    public SendGridProvider(ProviderConfig config) {
        // Validar que las credenciales necesarias estén presentes
        config.getRequiredProperty("apiKey");
        config.getRequiredProperty("fromEmail");
        this.config = config;
    }

    @Override
    public NotificationResult send(EmailNotification notification) {
        String apiKey = config.getRequiredProperty("apiKey");
        String fromEmail = config.getRequiredProperty("fromEmail");

        log.info("[SendGrid] Enviando email:");
        log.info("  From: {}", fromEmail);
        log.info("  To: {}", notification.getRecipient());
        log.info("  Subject: {}", notification.getSubject());
        log.info("  Body: {}", notification.getMessage());
        log.info("  API Key: {}...", apiKey.substring(0, Math.min(8, apiKey.length())));

        try {
            // --- SIMULACIÓN DEL ENVÍO ---
            // producción: HttpClient.send(POST, "https://api.sendgrid.com/v3/mail/send", body)
            String providerMessageId = "sg-" + UUID.randomUUID().toString().substring(0, 12);

            log.info("[SendGrid] Email enviado exitosamente. MessageId: {}", providerMessageId);
            return NotificationResult.success(notification.getId(), providerMessageId);

        } catch (Exception e) {
            log.error("[SendGrid] Error al enviar email: {}", e.getMessage());
            throw new SendException("Error en SendGrid: " + e.getMessage(), e);
        }
    }

    @Override
    public String getProviderName() {
        return "SendGrid";
    }
}
