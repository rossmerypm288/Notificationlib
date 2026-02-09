package com.notify.channel.push;

import com.notify.channel.push.provider.PushProvider;
import com.notify.core.ChannelType;
import com.notify.core.NotificationChannel;
import com.notify.core.NotificationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Canal de notificación por Push Notification.
 */
public class PushChannel implements NotificationChannel<PushNotification> {

    private static final Logger log = LoggerFactory.getLogger(PushChannel.class);

    private final PushProvider provider;

    public PushChannel(PushProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("El proveedor de Push no puede ser nulo");
        }
        this.provider = provider;
        log.info("PushChannel inicializado con proveedor: {}", provider.getProviderName());
    }

    @Override
    public NotificationResult send(PushNotification notification) {
        log.debug("Delegando envío push al proveedor: {}", provider.getProviderName());
        return provider.send(notification);
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.PUSH_NOTIFICATION;
    }

    @Override
    public boolean isAvailable() {
        return provider != null;
    }
}
