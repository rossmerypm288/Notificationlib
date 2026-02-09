package com.notify.core;

import java.util.concurrent.CompletableFuture;

/**
 * Interfaz principal que define el contrato de un canal de notificación.
 */
public interface NotificationChannel<T extends Notification> {

    /**
     * Envía una notificación de forma síncrona.
     *
     * @param notification La notificación a enviar
     * @return NotificationResult con el estado del envío
     */
    NotificationResult send(T notification);

    /**
     * Envía una notificación de forma asíncrona (no bloqueante).
     * Implementación por defecto que wrappea el método síncrono.
     *
     * Los canales pueden sobreescribir esto si su proveedor soporta
     * llamadas asíncronas nativas (ej: HTTP async con CompletableFuture).
     *
     * @param notification La notificación a enviar
     * @return CompletableFuture con el resultado del envío
     */
    default CompletableFuture<NotificationResult> sendAsync(T notification) {
        return CompletableFuture.supplyAsync(() -> send(notification));
    }

    /**
     * Indica qué tipo de canal maneja esta implementación.
     * Usado por el NotificationService para enrutar notificaciones.
     */
    ChannelType getChannelType();

    /**
     * Verifica si el canal está correctamente configurado y operativo.
     * Útil para health-checks y validación en startup.
     */
    boolean isAvailable();
}
