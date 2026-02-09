package com.notify.core;

import com.notify.exception.ChannelNotFoundException;
import com.notify.exception.ValidationException;
import com.notify.validation.NotificationValidator;
import com.notify.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Punto de entrada principal de la librería — Fachada (Facade Pattern).
 */
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    // Registro de canales: ChannelType -> NotificationChannel
    // Cada tipo de canal tiene una única implementación activa
    private final Map<ChannelType, NotificationChannel<?>> channels;

    // Validador de notificaciones
    private final NotificationValidator validator;

    /**
     * Constructor privado(se crea mediante Builder para control de configuración)
     */
    private NotificationService(Map<ChannelType, NotificationChannel<?>> channels,
                                NotificationValidator validator) {
        this.channels = new HashMap<>(channels);
        this.validator = validator;
    }

    /**
     * Envío SÍNCRONO de una notificación.
     *
     * @param notification Notificación a enviar (Email, SMS, Push)
     * @return NotificationResult con estado del envío
     * @throws ValidationException si la notificación tiene datos inválidos
     * @throws ChannelNotFoundException si no hay canal registrado para ese tipo
     */
    @SuppressWarnings("unchecked")
    public NotificationResult send(Notification notification) {
        log.info("Procesando notificación: {}", notification);

        //Valida la notificación
        validateNotification(notification);

        //Obtiene el canal adecuado
        NotificationChannel<Notification> channel = getChannelFor(notification.getChannelType());

        //Delega envío al canal y retornar resultado
        try {
            NotificationResult result = channel.send(notification);
            log.info("Resultado del envío: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error inesperado enviando notificación: {}", e.getMessage(), e);
            return NotificationResult.failure(notification.getId(), "Error interno: " + e.getMessage());
        }
    }

    /**
     * Envío ASÍNCRONO de una notificación (no bloqueante).
     *
     * @param notification Notificación a enviar
     * @return CompletableFuture con el resultado
     */
    public CompletableFuture<NotificationResult> sendAsync(Notification notification) {
        log.info("Procesando notificación asíncrona: {}", notification);

        try {
            validateNotification(notification);
            NotificationChannel<Notification> channel = getChannelFor(notification.getChannelType());
            return channel.sendAsync(notification);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                    NotificationResult.failure(notification.getId(), e.getMessage())
            );
        }
    }

    /**
     * Envío en LOTE: envía múltiples notificaciones de forma asíncrona.
     *
     * Cada notificación se envía en paralelo. Retorna una lista de resultados
     * cuando TODAS han completado (éxito o fallo).
     *
     * @param notifications Lista de notificaciones a enviar
     * @return CompletableFuture con lista de resultados
     */
    public CompletableFuture<List<NotificationResult>> sendBatch(List<Notification> notifications) {
        log.info("Enviando lote de {} notificaciones", notifications.size());

        List<CompletableFuture<NotificationResult>> futures = notifications.stream()
                .map(this::sendAsync)
                .collect(Collectors.toList());

        // CompletableFuture.allOf espera a que TODAS completen
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(ignored -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));
    }

    /**
     * Verifica si un canal específico está registrado y disponible.
     */
    public boolean isChannelAvailable(ChannelType type) {
        NotificationChannel<?> channel = channels.get(type);
        return channel != null && channel.isAvailable();
    }

    //Métodos privados auxiliares

    private void validateNotification(Notification notification) {
        ValidationResult validationResult = validator.validate(notification);
        if (!validationResult.isValid()) {
            throw new ValidationException(
                    "Notificación inválida: " + String.join(", ", validationResult.getErrors())
            );
        }
    }

    @SuppressWarnings("unchecked")
    private NotificationChannel<Notification> getChannelFor(ChannelType type) {
        NotificationChannel<?> channel = channels.get(type);
        if (channel == null) {
            throw new ChannelNotFoundException(
                    "No hay canal registrado para: " + type +
                    ". Canales disponibles: " + channels.keySet()
            );
        }
        return (NotificationChannel<Notification>) channel;
    }

    // --- Builder Pattern para construir el servicio ---

    /**
     * Inicia la construcción del servicio.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder que permite registrar canales y configurar el servicio.
     *
     * PATRÓN: Builder Pattern
     * - Construcción paso a paso del servicio
     * - Validación de configuración al construir
     * - API fluida (method chaining)
     */
    public static class Builder {

        private final Map<ChannelType, NotificationChannel<?>> channels = new HashMap<>();
        private NotificationValidator validator;

        /**
         * Registra un canal de notificación.
         * Si ya existe uno para ese ChannelType, lo reemplaza.
         */
        public Builder channel(NotificationChannel<?> channel) {
            this.channels.put(channel.getChannelType(), channel);
            return this;
        }

        /**
         * Permite inyectar un validador personalizado.
         * Si no se proporciona, se usa el validador por defecto.
         */
        public Builder validator(NotificationValidator validator) {
            this.validator = validator;
            return this;
        }

        /**
         * Construye el NotificationService con los canales registrados.
         *
         * @throws IllegalStateException si no hay canales registrados
         */
        public NotificationService build() {
            if (channels.isEmpty()) {
                throw new IllegalStateException(
                        "Debe registrar al menos un canal de notificación"
                );
            }

            // Si no se proporcionó validador, usar el por defecto
            if (validator == null) {
                validator = new NotificationValidator();
            }

            log.info("NotificationService creado con canales: {}", channels.keySet());
            return new NotificationService(channels, validator);
        }
    }
}
