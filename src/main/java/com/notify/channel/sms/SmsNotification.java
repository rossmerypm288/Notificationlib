package com.notify.channel.sms;

import com.notify.core.ChannelType;
import com.notify.core.Notification;

import java.util.Map;

/**
 * Notificación específica del canal SMS.
 *
 * Campos específicos basados en la API de Twilio:
 * - from: Número de origen (Twilio requiere un número comprado o MessagingServiceSid)
 * - El recipient (heredado) es el número destino en formato E.164 (+51999888777)
 * - El message (heredado) es el cuerpo del SMS (máx 160 chars para un segmento)
 *
 * REFERENCIA API TWILIO:
 *   POST https://api.twilio.com/2010-04-01/Accounts/{AccountSid}/Messages.json
 *   Body: To, From, Body
 *   Response: { "sid": "SM...", "status": "queued" }
 */
public class SmsNotification extends Notification {

    private final String from;  // Número de origen (ej: "+15551234567")

    private SmsNotification(Builder builder) {
        super(builder.to, builder.message, builder.metadata);
        this.from = builder.from;
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.SMS;
    }

    public String getFrom() {
        return from;
    }

    // --- Builder ---

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String to;
        private String message;
        private String from;
        private Map<String, String> metadata;

        public Builder to(String to) { this.to = to; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder from(String from) { this.from = from; return this; }
        public Builder metadata(Map<String, String> metadata) { this.metadata = metadata; return this; }

        public SmsNotification build() {
            return new SmsNotification(this);
        }
    }
}
