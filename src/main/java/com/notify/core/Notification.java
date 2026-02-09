package com.notify.core;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Clase base abstracta para todas las notificaciones.

 */
public abstract class Notification {

    // Identificador único generado automáticamente (para tracking y logs)
    private final String id;

    // Destinatario: puede ser email, teléfono, deviceToken según el canal
    private final String recipient;

    // Cuerpo del mensaje a enviar
    private final String message;

    // Momento de creación de la notificación
    private final LocalDateTime createdAt;

    // extensible: agrega datos adicionales sin modificar la clase
    // {"priority": "high", "campaign": "black-friday"}
    private final Map<String, String> metadata;

    /**
     * Constructor protegido: solo accesible por subclases.
     * Genera automáticamente el ID y timestamp de creación.
     */
    protected Notification(String recipient, String message, Map<String, String> metadata) {
        this.id = UUID.randomUUID().toString();
        this.recipient = recipient;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
    }

    /**
     * Cada subclase debe indicar a qué canal pertenece.
     * Esto permite al NotificationService enrutar al canal correcto.
     */
    public abstract ChannelType getChannelType();

    //Getters (sin Lombok )

    public String getId() {
        return id;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Map<String, String> getMetadata() {
        return new HashMap<>(metadata); // Copia defensiva: evita mutación externa
    }

    @Override
    public String toString() {
        return String.format("[%s] id=%s, to=%s, message=%s",
                getChannelType(), id, recipient, message);
    }
}
