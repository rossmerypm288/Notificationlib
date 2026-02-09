package com.notify.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuración de un proveedor específico (SendGrid, Twilio, Firebase, etc.).
 */
public class ProviderConfig {

    private final String providerName;             // Nombre del proveedor (ej: "sendgrid", "twilio")
    private final Map<String, String> properties;  // Propiedades de configuración (apiKey)

    private ProviderConfig(String providerName, Map<String, String> properties) {
        this.providerName = providerName;
        this.properties = Collections.unmodifiableMap(new HashMap<>(properties));
    }

    /**
     * Obtiene una propiedad de configuración.
     *
     * @param key Nombre de la propiedad
     * @return Valor o null si no existe
     */
    public String getProperty(String key) {
        return properties.get(key);
    }

    /**
     * Obtiene una propiedad obligatoria. Lanza excepción si no existe.
     * Para validar que las credenciales necesarias están presentes.
     */
    public String getRequiredProperty(String key) {
        String value = properties.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Propiedad requerida '" + key + "' no configurada para proveedor: " + providerName
            );
        }
        return value;
    }

    public String getProviderName() {
        return providerName;
    }

    /**
     * toString seguro: muestra las CLAVES pero NO los valores (protege credenciales).
     */
    @Override
    public String toString() {
        return "ProviderConfig[" + providerName + ", keys=" + properties.keySet() + "]";
    }

    // --- Builder ---

    public static Builder builder(String providerName) {
        return new Builder(providerName);
    }

    public static class Builder {
        private final String providerName;
        private final Map<String, String> properties = new HashMap<>();

        private Builder(String providerName) {
            if (providerName == null || providerName.isBlank()) {
                throw new IllegalArgumentException("El nombre del proveedor es obligatorio");
            }
            this.providerName = providerName;
        }

        public Builder property(String key, String value) {
            this.properties.put(key, value);
            return this;
        }

        public ProviderConfig build() {
            return new ProviderConfig(providerName, properties);
        }
    }
}
