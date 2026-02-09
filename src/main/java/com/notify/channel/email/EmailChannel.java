package com.notify.channel.email;

import com.notify.channel.email.provider.EmailProvider;
import com.notify.core.ChannelType;
import com.notify.core.NotificationChannel;
import com.notify.core.NotificationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Canal de notificación por Email.
 */
public class EmailChannel implements NotificationChannel<EmailNotification> {

    private static final Logger log = LoggerFactory.getLogger(EmailChannel.class);

    // Proveedor activo (inyectado por constructor — DIP)
    private final EmailProvider provider;

    /**
     * Constructor: recibe el proveedor a utilizar.
     * El consumidor decide qué proveedor usar al configurar el canal.
     *
     *   new EmailChannel(new SendGridProvider(config))
     *   new EmailChannel(new MailgunProvider(config))
     */
    public EmailChannel(EmailProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("El proveedor de email no puede ser nulo");
        }
        this.provider = provider;
        log.info("EmailChannel inicializado con proveedor: {}", provider.getProviderName());
    }

    @Override
    public NotificationResult send(EmailNotification notification) {
        log.debug("Delegando envío de email al proveedor: {}", provider.getProviderName());
        return provider.send(notification);
    }

    @Override
    public ChannelType getChannelType() {
        return ChannelType.EMAIL;
    }

    @Override
    public boolean isAvailable() {
        // verifica conectividad con el proveedor (health check)
        return provider != null;
    }
}
