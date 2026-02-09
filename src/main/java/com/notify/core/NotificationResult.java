package com.notify.core;

import java.time.LocalDateTime;

/**
 * Encapsula el resultado de un intento de envío de notificación.
 */
public class NotificationResult {

    private final String notificationId;
    private final NotificationStatus status;
    private final String providerMessageId;  // ID que retorna el proveedor
    private final String errorMessage;       // Mensaje de error si falló
    private final LocalDateTime processedAt;

    // se crean mediante factory methods estáticos

    private NotificationResult(String notificationId, NotificationStatus status,
                               String providerMessageId, String errorMessage) {
        this.notificationId = notificationId;
        this.status = status;
        this.providerMessageId = providerMessageId;
        this.errorMessage = errorMessage;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * Factory method para resultado exitoso.
     * Requiere el ID del proveedor para tracking.
     *
     * NotificationResult.success
     */
    public static NotificationResult success(String notificationId, String providerMessageId) {
        return new NotificationResult(notificationId, NotificationStatus.SENT, providerMessageId, null);
    }

    /**
     * Factory method para resultado fallido.
     * Incluye mensaje de error descriptivo.
     *
     * NotificationResult.failure("notif-123", "Invalid email format")
     */
    public static NotificationResult failure(String notificationId, String errorMessage) {
        return new NotificationResult(notificationId, NotificationStatus.FAILED, null, errorMessage);
    }

    /**
     * Verificación rápida de éxito — evita comparar enums manualmente.
     */
    public boolean isSuccess() {
        return status == NotificationStatus.SENT;
    }

    // Getters

    public String getNotificationId() {
        return notificationId;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public String getProviderMessageId() {
        return providerMessageId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    @Override
    public String toString() {
        if (isSuccess()) {
            return String.format("Result[SUCCESS] notifId=%s, providerId=%s",
                    notificationId, providerMessageId);
        }
        return String.format("Result[FAILED] notifId=%s, error=%s",
                notificationId, errorMessage);
    }
}
