package com.notify.core;

/**
 * Estados posibles del ciclo de vida de una notificación.
 */
public enum NotificationStatus {

    PENDING,    // Notificación creada, esperando ser enviada
    SENT,       // Envío exitoso confirmado por el proveedor
    FAILED,     // Error en el envío (validación o proveedor)
    RETRYING    // Reintentando tras un fallo temporal
}
