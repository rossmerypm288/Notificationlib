package com.notify.channel.sms;

import com.notify.channel.sms.provider.SmsProvider;
import com.notify.core.ChannelType;
import com.notify.core.NotificationChannel;
import com.notify.core.NotificationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Canal de notificación por SMS.
 */
public class SmsChannel implements NotificationChannel<SmsNotification> {

    private static final Logger log = LoggerFactory.getLogger(SmsChannel.class);

    private final SmsProvider provider;

    public SmsChannel(SmsProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("El proveedor de SMS no puede ser nulo");
        }
        this.provider = provider;
        log.info("SmsChannel inicializado con proveedor: {}", provider.getProviderName());
    }

    @Override
    public NotificationResult send(SmsNotification notification) {
        log.debug("Delegando envío SMS al proveedor: {}", provider.getProviderName());
        return provider.send(notification);
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.SMS;
    }

    @Override
    public boolean isAvailable() {
        return provider != null;
    }
}
