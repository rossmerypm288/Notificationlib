package com.notify.retry;

import com.notify.core.NotificationResult;
import com.notify.exception.SendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * Ejecutor de reintentos — aplica RetryPolicy a cualquier operación de envío.
 */
public class RetryExecutor {

    private static final Logger log = LoggerFactory.getLogger(RetryExecutor.class);

    private final RetryPolicy policy;

    public RetryExecutor(RetryPolicy policy) {
        this.policy = policy;
    }

    /**
     * Ejecuta una operación con reintentos según la política configurada.
     *
     * @param operation Operación a ejecutar (lambda que envía la notificación)
     * @return NotificationResult del intento exitoso
     * @throws SendException si todos los intentos fallan
     */
    public NotificationResult execute(Supplier<NotificationResult> operation) {
        Exception lastException = null;

        for (int attempt = 1; attempt <= policy.getMaxAttempts(); attempt++) {
            try {
                log.debug("Intento {}/{}", attempt, policy.getMaxAttempts());

                NotificationResult result = operation.get();

                // el resultado indica fallo pero no lanzó excepción, reintentar
                if (result.isSuccess()) {
                    return result;
                }

                log.warn("Intento {} falló con resultado: {}", attempt, result);
                lastException = new SendException(result.getErrorMessage());

            } catch (SendException e) {
                lastException = e;
                log.warn("Intento {} lanzó excepción: {}", attempt, e.getMessage());
            }

            // Espera antes del siguiente intento (excepto en el último)
            if (attempt < policy.getMaxAttempts()) {
                long delay = policy.getDelayForAttempt(attempt);
                log.info("Esperando {}ms antes del siguiente intento...", delay);
                sleep(delay);
            }
        }

        // aquí, todos los intentos fallaron
        log.error("Todos los {} intentos fallaron", policy.getMaxAttempts());
        throw new SendException(
                "Falló después de " + policy.getMaxAttempts() + " intentos: " +
                (lastException != null ? lastException.getMessage() : "Error desconocido"),
                lastException
        );
    }

    /**
     * Sleep con manejo de InterruptedException.
     */
    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SendException("Reintento interrumpido", e);
        }
    }
}
