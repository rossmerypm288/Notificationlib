package com.notify.channel.email.provider;

import com.notify.channel.email.EmailNotification;
import com.notify.core.NotificationResult;

/**
 * Contrato que todo proveedor de Email debe cumplir.
 */
public interface EmailProvider {

    /**
     * Envía un email a través de este proveedor.
     *
     * @param notification Email a enviar
     * @return Resultado con messageId del proveedor
     */
    NotificationResult send(EmailNotification notification);

    /**
     * Nombre identificador del proveedor (para logs y debugging).
     */
    String getProviderName();
}
