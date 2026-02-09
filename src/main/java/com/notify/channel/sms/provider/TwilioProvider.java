package com.notify.channel.sms.provider;

import com.notify.channel.sms.SmsNotification;
import com.notify.config.ProviderConfig;
import com.notify.core.NotificationResult;
import com.notify.exception.SendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Proveedor de SMS: Twilio.
 */
public class TwilioProvider implements SmsProvider {

    private static final Logger log = LoggerFactory.getLogger(TwilioProvider.class);

    private final ProviderConfig config;

    public TwilioProvider(ProviderConfig config) {
        config.getRequiredProperty("accountSid");
        config.getRequiredProperty("authToken");
        config.getRequiredProperty("fromNumber");
        this.config = config;
    }

    @Override
    public NotificationResult send(SmsNotification notification) {
        String fromNumber = config.getRequiredProperty("fromNumber");

        log.info("[Twilio] Enviando SMS:");
        log.info("  From: {}", fromNumber);
        log.info("  To: {}", notification.getRecipient());
        log.info("  Body: {}", notification.getMessage());

        try {
            //SIMULACIÓN
            //POST a Twilio REST API con Basic Auth
            String messageSid = "SM" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

            log.info("[Twilio] SMS encolado. SID: {}", messageSid);
            return NotificationResult.success(notification.getId(), messageSid);

        } catch (Exception e) {
            log.error("[Twilio] Error al enviar SMS: {}", e.getMessage());
            throw new SendException("Error en Twilio: " + e.getMessage(), e);
        }
    }

    @Override
    public String getProviderName() {
        return "Twilio";
    }
}
