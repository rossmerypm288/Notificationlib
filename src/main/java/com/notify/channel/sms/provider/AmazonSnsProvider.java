package com.notify.channel.sms.provider;

import com.notify.channel.sms.SmsNotification;
import com.notify.config.ProviderConfig;
import com.notify.core.NotificationResult;
import com.notify.exception.SendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Proveedor de SMS: Amazon SNS (simulado).
 *
 * SIMULACIÓN basada en la API real de AWS SNS:
 * - Requiere: accessKey y secretKey (credenciales IAM)
 * - Requiere: region (ej: "us-east-1")
 * - Endpoint real: POST https://sns.{region}.amazonaws.com
 * - Action: Publish
 * - Response: XML con <MessageId> UUID </MessageId>
 *
 * Demuestra que se puede cambiar de Twilio a AWS SNS transparentemente.
 */
public class AmazonSnsProvider implements SmsProvider {

    private static final Logger log = LoggerFactory.getLogger(AmazonSnsProvider.class);

    private final ProviderConfig config;

    public AmazonSnsProvider(ProviderConfig config) {
        config.getRequiredProperty("accessKey");
        config.getRequiredProperty("secretKey");
        config.getRequiredProperty("region");
        this.config = config;
    }

    @Override
    public NotificationResult send(SmsNotification notification) {
        String region = config.getRequiredProperty("region");

        log.info("[Amazon SNS] Enviando SMS via región: {}", region);
        log.info("  To: {}", notification.getRecipient());
        log.info("  Body: {}", notification.getMessage());

        try {
            //SIMULACIÓN
            //AWS SDK SNSClient.publish(PublishRequest)
            String messageId = "sns-" + UUID.randomUUID().toString().substring(0, 12);

            log.info("[Amazon SNS] SMS publicado. MessageId: {}", messageId);
            return NotificationResult.success(notification.getId(), messageId);

        } catch (Exception e) {
            log.error("[Amazon SNS] Error: {}", e.getMessage());
            throw new SendException("Error en Amazon SNS: " + e.getMessage(), e);
        }
    }

    @Override
    public String getProviderName() {
        return "Amazon SNS";
    }
}
