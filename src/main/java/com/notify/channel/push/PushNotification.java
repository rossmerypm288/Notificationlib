package com.notify.channel.push;

import com.notify.core.ChannelType;
import com.notify.core.Notification;

import java.util.Map;

/**
 * Notificación específica del canal Push Notification.
 */
public class PushNotification extends Notification {

    private final String title;                // Título de la notificación
    private final String imageUrl;             // URL de imagen
    private final Map<String, String> data;    // Payload personalizado para la app

    private PushNotification(Builder builder) {
        super(builder.deviceToken, builder.message, builder.metadata);
        this.title = builder.title;
        this.imageUrl = builder.imageUrl;
        this.data = builder.data != null ? Map.copyOf(builder.data) : Map.of();
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.PUSH_NOTIFICATION;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Map<String, String> getData() {
        return data;
    }

    //Builder

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String deviceToken;   // Se almacena como 'recipient' en Notification
        private String message;
        private String title;
        private String imageUrl;
        private Map<String, String> data;
        private Map<String, String> metadata;

        public Builder deviceToken(String deviceToken) { this.deviceToken = deviceToken; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public Builder data(Map<String, String> data) { this.data = data; return this; }
        public Builder metadata(Map<String, String> metadata) { this.metadata = metadata; return this; }

        public PushNotification build() {
            return new PushNotification(this);
        }
    }
}
