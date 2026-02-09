package com.notify.channel.email.provider;

import com.notify.channel.email.EmailNotification;
import com.notify.config.ProviderConfig;
import com.notify.core.NotificationResult;
import com.notify.exception.SendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Proveedor de email: Mailgun (simulado).
 */
public class MailgunProvider implements EmailProvider {

    private static final Logger log = LoggerFactory.getLogger(MailgunProvider.class);

    private final ProviderConfig config;

    public MailgunProvider(ProviderConfig config) {
        config.getRequiredProperty("apiKey");
        config.getRequiredProperty("domain");
        this.config = config;
    }

    @Override
    public NotificationResult send(EmailNotification notification) {
        String domain = config.getRequiredProperty("domain");

        log.info("[Mailgun] Enviando email via dominio: {}", domain);
        log.info("  To: {}", notification.getRecipient());
        log.info("  Subject: {}", notification.getSubject());

        try {
            // --- SIMULACIÓN ---
            // En producción: POST https://api.mailgun.net/v3/{domain}/messages
            String providerMessageId = "mg-" + UUID.randomUUID().toString().substring(0, 12);

            log.info("[Mailgun] Email encolado exitosamente. MessageId: {}", providerMessageId);
            return NotificationResult.success(notification.getId(), providerMessageId);

        } catch (Exception e) {
            log.error("[Mailgun] Error al enviar: {}", e.getMessage());
            throw new SendException("Error en Mailgun: " + e.getMessage(), e);
        }
    }

    @Override
    public String getProviderName() {
        return "Mailgun";
    }
}
