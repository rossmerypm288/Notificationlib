package com.notify.retry;

/**
 * Política de reintentos configurable.
 */
public class RetryPolicy {

    private final int maxAttempts;       // Máximo número de reintentos
    private final long initialDelayMs;   // Delay inicial en milisegundos
    private final double multiplier;     // Factor multiplicador

    private RetryPolicy(int maxAttempts, long initialDelayMs, double multiplier) {
        this.maxAttempts = maxAttempts;
        this.initialDelayMs = initialDelayMs;
        this.multiplier = multiplier;
    }

    /**
     * Política por defecto: 3 intentos, 1 segundo inicial, backoff x2.
     * Tiempos: 1s → 2s → 4s
     */
    public static RetryPolicy defaultPolicy() {
        return new RetryPolicy(3, 1000, 2.0);
    }

    /**
     * Sin reintentos — falla inmediatamente al primer error.
     */
    public static RetryPolicy noRetry() {
        return new RetryPolicy(1, 0, 1.0);
    }

    /**
     * Calcula el delay para un intento específico.
     * Intento 1: initialDelay
     * Intento 2: initialDelay * multiplier
     * Intento 3: initialDelay * multiplier^2
     */
    public long getDelayForAttempt(int attempt) {
        return (long) (initialDelayMs * Math.pow(multiplier, attempt - 1));
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public long getInitialDelayMs() {
        return initialDelayMs;
    }

    public double getMultiplier() {
        return multiplier;
    }

    //Builder

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int maxAttempts = 3;
        private long initialDelayMs = 1000;
        private double multiplier = 2.0;

        public Builder maxAttempts(int maxAttempts) { this.maxAttempts = maxAttempts; return this; }
        public Builder initialDelayMs(long initialDelayMs) { this.initialDelayMs = initialDelayMs; return this; }
        public Builder multiplier(double multiplier) { this.multiplier = multiplier; return this; }

        public RetryPolicy build() {
            if (maxAttempts < 1) throw new IllegalArgumentException("maxAttempts debe ser >= 1");
            if (initialDelayMs < 0) throw new IllegalArgumentException("initialDelayMs debe ser >= 0");
            if (multiplier < 1.0) throw new IllegalArgumentException("multiplier debe ser >= 1.0");
            return new RetryPolicy(maxAttempts, initialDelayMs, multiplier);
        }
    }
}
